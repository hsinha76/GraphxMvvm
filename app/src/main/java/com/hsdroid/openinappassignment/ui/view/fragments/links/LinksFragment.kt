package com.hsdroid.openinappassignment.ui.view.fragments.links

import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import com.hsdroid.openinappassignment.R
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.hsdroid.openinappassignment.data.models.ServerResponse
import com.hsdroid.openinappassignment.databinding.FragmentLinksBinding
import com.hsdroid.openinappassignment.ui.view.adapter.RecentLinksAdapter
import com.hsdroid.openinappassignment.ui.view.adapter.TopLinksAdapter
import com.hsdroid.openinappassignment.ui.viewmodel.HomeViewModel
import com.hsdroid.openinappassignment.utils.APIState
import com.hsdroid.openinappassignment.utils.Helper.Companion.createGradientDrawable
import com.hsdroid.openinappassignment.utils.Helper.Companion.getGreetingMessage
import com.hsdroid.openinappassignment.utils.Helper.Companion.setViewsVisibility
import com.hsdroid.openinappassignment.utils.Helper.Companion.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class LinksFragment : Fragment() {

    private lateinit var binding: FragmentLinksBinding
    private val homeViewModel by activityViewModels<HomeViewModel>()

    private val topLinksAdapter by lazy {
        TopLinksAdapter()
    }

    private val recentLinksAdapter by lazy {
        RecentLinksAdapter()
    }

    //Graph elements
    private lateinit var lineList: ArrayList<Entry>
    private lateinit var lineDataSet: LineDataSet
    private lateinit var lineData: LineData
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLinksBinding.inflate(inflater)

        initViews()
        collectFlows()
        return binding.root
    }

    private fun initViews() {
        with(binding) {
            recyclerviewTopLinks.apply {
                adapter = topLinksAdapter
            }

            recyclerviewRecentLinks.apply {
                adapter = recentLinksAdapter
            }
        }
    }

    private fun collectFlows() {
        lifecycleScope.launch {
            homeViewModel.response.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest {
                    when (it) {
                        is APIState.LOADING -> loadingView()
                        is APIState.FAILURE -> errorView(it.t)
                        is APIState.SUCCESS -> setData(it.data)
                        else -> Unit
                    }
                }
        }
    }

    private fun loadingView() {
        with(binding) {
            setViewsVisibility(
                View.VISIBLE,
                viewAnimation
            )
            setViewsVisibility(
                View.GONE,
                viewError,
                viewSuccess
            )
        }
    }

    private fun errorView(t: Throwable) {
        with(binding) {
            setViewsVisibility(
                View.VISIBLE,
                viewError
            )
            setViewsVisibility(
                View.GONE,
                viewAnimation,
                viewSuccess
            )

            btnRetry.setOnClickListener {
                homeViewModel.getResponse()
            }
        }

        showToast(context, t.message.toString())
    }

    private fun setData(data: ServerResponse) {
        with(binding) {

            lifecycleScope.launch {
                delay(1000)
                setViewsVisibility(
                    View.VISIBLE,
                    viewSuccess
                )
                setViewsVisibility(
                    View.GONE,
                    viewAnimation,
                    viewError
                )
            }

            tvGreetings.text = getGreetingMessage()
            tvTotalClicks.text = data.today_clicks.toString()
            tvTopSource.text = data.top_source
            tvTopLocation.text = data.top_location

            radioGroup.setOnCheckedChangeListener { radioGroup, optionId ->
                run {
                    when (optionId) {
                        R.id.btn_top_links -> {
                            setViewsVisibility(
                                View.GONE,
                                recyclerviewRecentLinks
                            )
                            setViewsVisibility(
                                View.VISIBLE,
                                recyclerviewTopLinks
                            )
                        }

                        R.id.btn_recent_links -> {
                            setViewsVisibility(
                                View.GONE,
                                recyclerviewTopLinks
                            )
                            setViewsVisibility(
                                View.VISIBLE,
                                recyclerviewRecentLinks
                            )
                        }
                    }
                }
            }
        }

        topLinksAdapter.setList(data.data.top_links)
        recentLinksAdapter.setList(data.data.recent_links)

        //Graph init
        setGraphValues(data.data.overall_url_chart)
    }

    @SuppressLint("SetTextI18n")
    private fun setGraphValues(urlChartResponse: Map<String, Int>?) {
        lineList = ArrayList()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val startDate = dateFormat.parse(urlChartResponse?.keys?.minOrNull()!!)
        val endDate = dateFormat.parse(urlChartResponse.keys.maxOrNull()!!)
        val calendar = Calendar.getInstance()

        val textFormat = SimpleDateFormat("d MMM", Locale.US)
        binding.tvDuration.text = textFormat.format(startDate!!) + "-" + textFormat.format(
            endDate!!
        )

        urlChartResponse.forEach { (key, value) ->
            val date = dateFormat.parse(key)
            if (date != null) {
                calendar.time = date
            }

            if (date!! in startDate..endDate) {
                val daysSinceStart =
                    TimeUnit.MILLISECONDS.toDays(date.time - startDate.time).toFloat()
                lineList.add(Entry(daysSinceStart, value.toFloat()))
            }
        }

        lineDataSet = LineDataSet(lineList, null).apply {
            color = Color.parseColor("#0E6FFF")
            setDrawValues(false)
            setDrawCircles(false)
            setDrawFilled(true)
            fillDrawable = createGradientDrawable(Color.parseColor("#0E6FFF"), Color.TRANSPARENT)
        }

        lineData = LineData(lineDataSet)
        binding.chartView.data = lineData

        val xAxis = binding.chartView.xAxis
        xAxis?.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawAxisLine(true)
            setDrawLabels(true)
            valueFormatter = object : ValueFormatter() {
                private val format = SimpleDateFormat("d MMM", Locale.US)
                override fun getFormattedValue(value: Float): String {
                    calendar.time = startDate
                    calendar.add(Calendar.DAY_OF_YEAR, value.toInt())
                    return if (value.toInt() % 5 == 0) format.format(calendar.time) else ""
                }
            }
            granularity = 1f
            labelCount = TimeUnit.MILLISECONDS.toDays(endDate.time - startDate.time).toInt()
        }

        with(binding) {
            chartView.axisRight?.isEnabled = false
            chartView.description?.isEnabled = false
            chartView.legend?.isEnabled = false
            chartView.invalidate()
        }
    }
}