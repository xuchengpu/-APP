package com.cainiao.course.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.databinding.ObservableInt
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.viewbinding.ViewBinding
import com.blankj.utilcode.util.ToastUtils
import com.cainiao.common.base.BaseFragment
import com.cainiao.course.R
import com.cainiao.course.databinding.FragmentCourseBinding
import com.cainiao.course.databinding.PopCourseTypeBinding
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.flow.collect
import org.koin.androidx.viewmodel.ext.android.viewModel


/**
 * Created by 许成谱 on 2021/1/13 18:32.
 * qq:1550540124
 * 热爱生活每一天！
 */
class CourseFragment : BaseFragment() {
    private val viewModel: CourseViewModel by viewModel()
    private lateinit var mBinding: FragmentCourseBinding
    private var code = "all"
    override fun bindView(view: View, savedInstanceState: Bundle?): ViewDataBinding? {
        mBinding = FragmentCourseBinding.bind(view)
        return mBinding.apply {
            val adapter = viewModel.courseAdapter
            rvCourse.adapter = adapter.withLoadStateFooter(CourseLoadAdapter(adapter))

            viewModel.apply {
                liveCourseType.observeKt { types ->
                    tlCategoryCourse.removeAllTabs()
                    val tab = tlCategoryCourse.newTab().also { tab ->
                        tab.text = "全部"
                    }
                    tlCategoryCourse.addTab(tab)

                    types?.forEach { item ->
                        tlCategoryCourse.addTab(
                            tlCategoryCourse.newTab().also { tab ->
                                tab.text = item.title
                            }
                        )

                    }
                    tlCategoryCourse.selectTab(tab)

                }
                tlCategoryCourse.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

                    override fun onTabSelected(tab: TabLayout.Tab?) {
                        val courseTypes = liveCourseType.value
                        val count = tlCategoryCourse.tabCount

                        val index = tab?.position ?: 0
                        code = if (index > 0) {
                            courseTypes?.get(index - 1)?.code ?: "all"
                        } else "all"
                        refreshData(code)
                    }

                    override fun onTabUnselected(tab: TabLayout.Tab?) {}

                    override fun onTabReselected(tab: TabLayout.Tab?) {}

                })

            }
            adapter.addLoadStateListener { loadState ->
                if (loadState.refresh is LoadState.Loading) {
                    pbFragmentCourse.visibility = View.VISIBLE
                } else {
                    pbFragmentCourse.visibility = View.GONE
                }
                // getting the error
                val error = when {
                    loadState.prepend is LoadState.Error -> loadState.prepend as LoadState.Error
                    loadState.append is LoadState.Error -> loadState.append as LoadState.Error
                    loadState.refresh is LoadState.Error -> loadState.refresh as LoadState.Error
                    else -> null
                }
                error?.let {
                    ToastUtils.showShort(it.error.message)
                }

            }
            spinnerTypeCourse.setOnClickListener {
                popWindow.showAsDropDown(it)
            }
        }
    }

    private fun refreshData(code: String, courseType: Int = -1) {
        lifecycleScope.launchWhenCreated {
            viewModel.apply {
                getCourseList(code = code, course_type = courseType).collect {
                    viewModel.courseAdapter.submitData(it)
                    mBinding.rvCourse.scrollToPosition(0)
                }
            }
        }
    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_course
    }

    override fun initConfig() {
        super.initConfig()
        viewModel.getCourseCategory()
        configPopFilter()
    }

    private lateinit var popWindow: PopupWindow
    private fun configPopFilter() {
        val popBinding =
            PopCourseTypeBinding.inflate(LayoutInflater.from(requireContext()), null, false)
        popWindow = PopupWindow(
            popBinding.root,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val obCheckPos = ObservableInt(0)
        popBinding.apply {
            pos = obCheckPos
            tvAllType.setOnClickListener {
                obCheckPos.set(0)
                popWindow.dismiss()
                refreshData(code)
            }
            tvBizType.setOnClickListener {
                obCheckPos.set(1)
                popWindow.dismiss()
                refreshData(code, 1)
            }
            tvProType.setOnClickListener {
                obCheckPos.set(2)
                popWindow.dismiss()
                refreshData(code, 2)
            }


        }


    }

}