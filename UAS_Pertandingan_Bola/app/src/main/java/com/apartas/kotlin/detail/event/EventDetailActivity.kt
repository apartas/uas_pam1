package com.apartas.kotlin.detail.event

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.view.MenuItem
import android.widget.ProgressBar
import com.google.gson.Gson
import com.apartas.kotlin.R
import com.apartas.kotlin.api.ApiRepository
import com.apartas.kotlin.model.EventDetail
import com.apartas.kotlin.util.invisible
import com.apartas.kotlin.util.visible
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_event_detail.*
import okhttp3.*
import org.jetbrains.anko.ctx
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.onRefresh
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EventDetailActivity : AppCompatActivity(), EventDetailView {
    private lateinit var presenter: EventDetailPresenter
    private lateinit var idEvent: String
    private var idHome: String = ""
    private var idAway: String = ""
    private lateinit var progressBar: ProgressBar
    private lateinit var swipeRefresh: SwipeRefreshLayout
    val client = OkHttpClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)
        supportActionBar?.title = "Match Detail"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        progressBar = find(R.id.progress_bar_detail)
        swipeRefresh = find(R.id.swipe_refresh_detail)

        val intent = intent
        idEvent = intent.getStringExtra("id")
        idHome = intent.getStringExtra("idhome")
        idAway = intent.getStringExtra("idaway")

        val request = ApiRepository()
        val gson = Gson()
        presenter = EventDetailPresenter(this, request, gson)
        presenter.getEventDetail(idEvent)
        swipeRefresh.onRefresh {
            presenter.getEventDetail(idEvent)
        }
        val logo = arrayOf(idHome, idAway)
        getBadge(logo)
    }

    override fun showLoading() {
        progressBar.visible()
    }

    override fun hideLoading() {
        progressBar.invisible()
    }

    override fun showEventDetail(data: List<EventDetail>) {
        swipeRefresh.isRefreshing = false
        val tanggal = SimpleDateFormat("EEE, d MMM yyyy")
                .format(SimpleDateFormat("yyyy-MM-dd")
        val waktu = SimpleDateFormat("HH:mm")
                .format(SimpleDateFormat("H:i:s")
        tv_date_detail.text = tanggal
        tv_time_detail.text = waktu
        tv_home_detail.text = data[0].teamHome
        tv_away_detail.text = data[0].teamAway
        if(data[0].scoreHome.isNullOrEmpty() && data[0].scoreAway.isNullOrEmpty()){
            tv_skor_detail.text = "0 vs 0"
        }else{
            tv_skor_detail.text = data[0].scoreHome+"  vs  "+data[0].scoreAway
        }
        tv_home_formation.text = data[0].formationHome
        tv_away_formation.text = data[0].formationAway
        tv_home_goals.text = data[0].goalHome?.replace(";", "\n")
        tv_away_goals.text = data[0].goalAway?.replace(";", "\n")
        tv_home_shots.text = data[0].shotHome
        tv_away_shots.text = data[0].shotAway
    }

    private fun getBadge(logo: Array<String>) {
        for(i in 0..1){
            val request = Request.Builder()
                    .url("https://www.thesportsdb.com/api/v1/json/1/lookupteam.php?id="+logo[i])
                    .build()
            client.newCall(request).enqueue(object : Callback{
                override fun onFailure(call: Call, e: IOException) {}
                override fun onResponse(call: Call, response: Response) {
                    var a = response.body()?.string()
                    runOnUiThread {
                        run(){
                            var json = JSONObject(a)
                            var badge = json.getJSONArray("teams").getJSONObject(0).getString("strTeamBadge")
                            if(i == 0) {
                                Picasso.with(ctx).load(badge).into(img_home)
                            }else{
                                Picasso.with(ctx).load(badge).into(img_away)
                            }
                        }
                    }
                }
            })
        }
    }
}


