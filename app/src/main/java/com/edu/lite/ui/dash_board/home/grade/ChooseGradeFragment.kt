package com.edu.lite.ui.dash_board.home.grade

import android.annotation.SuppressLint
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.edu.lite.BR
import com.edu.lite.R
import com.edu.lite.base.BaseFragment
import com.edu.lite.base.BaseViewModel
import com.edu.lite.base.SimpleRecyclerViewAdapter
import com.edu.lite.data.api.Constants
import com.edu.lite.data.model.GradeData
import com.edu.lite.data.model.GradeModelResponse
import com.edu.lite.data.model.SignupResponse
import com.edu.lite.databinding.FragmentChooseGradeBinding
import com.edu.lite.databinding.RvGradeItemBinding
import com.edu.lite.ui.auth.forgot.OtpFragmentDirections
import com.edu.lite.ui.auth.languages.LanguageFragmentArgs
import com.edu.lite.ui.dash_board.home.HomeFragmentVM
import com.edu.lite.utils.BindingUtils
import com.edu.lite.utils.Status
import dagger.hilt.android.AndroidEntryPoint
import kotlin.getValue

@AndroidEntryPoint
class ChooseGradeFragment : BaseFragment<FragmentChooseGradeBinding>() {
    private val viewModel: HomeFragmentVM by viewModels()
    private lateinit var gradeAdapter: SimpleRecyclerViewAdapter<GradeData, RvGradeItemBinding>
    private val args: ChooseGradeFragmentArgs by navArgs()
    private var from :String?=null

    override fun getLayoutResource(): Int {
        return R.layout.fragment_choose_grade
    }

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun onCreateView(view: View) {
        // adapter
        initOnGradeAdapter()
        //
        from = args.from
        if (from!=null && from =="home"){
            binding.tvChoose.text=getString(R.string.choose_grade)
        }
        else{
            binding.tvChoose.text=getString(R.string.choose_your_grade)
        }

        if (from !=null && from =="settings"){
            binding.clGrade.visibility = View.VISIBLE
            if (sharedPrefManager.getLoginData()!=null){
                val data = sharedPrefManager.getLoginData()?.gradeId
                if ( data!= null) {
                    Glide.with(requireContext()).load(Constants.BASE_URL_IMAGE + data.icon).placeholder(R.drawable.progress_drawable).into(binding.ivEarlyGrade)
                    binding.tvGrade.text= data.grade
                }

            }

        }
        // click
        initOnClick()
        // observer
        initObserver()
        // api call
        viewModel.getGradeApi(Constants.GRADES)
    }

    /** api response observer ***/
    private fun initObserver() {
        viewModel.observeCommon.observe(viewLifecycleOwner) {
            when (it?.status) {
                Status.LOADING -> {
                    showLoading()
                }

                Status.SUCCESS -> {
                    when (it.message) {
                        "getGradeApi" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: GradeModelResponse? = BindingUtils.parseJson(jsonData)
                                val grades = model?.grades
                                if (!grades.isNullOrEmpty()) {
                                    gradeAdapter.list = grades
                                } else {
                                    showErrorToast("No grades found.")
                                }
                            }.onFailure { e ->
                                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                                showErrorToast(e.message.toString())
                            }.also {
                                hideLoading()
                            }
                        }

                        "updateProfileApi" -> {
                            runCatching {
                                val jsonData = it.data?.toString().orEmpty()
                                val model: SignupResponse? = BindingUtils.parseJson(jsonData)
                                val loginData = model?.user
                                if (loginData!=null) {
                                    loginData.let { it1 ->
                                        sharedPrefManager.setLoginData(it1)
                                    }
                                }
                                if (from!=null && from =="settings"){
                                    findNavController().popBackStack()
                                }
                                else{
                                    val action = OtpFragmentDirections.navigateToHomeFragment()
                                    BindingUtils.navigateWithSlide(
                                        findNavController(), action
                                    )
                                }

                            }.onFailure { e ->
                                Log.e("apiErrorOccurred", "Error: ${e.message}", e)
                                showErrorToast(e.message.toString())
                            }.also {
                                hideLoading()
                            }
                        }
                    }
                }

                Status.ERROR -> {
                    hideLoading()
                    showErrorToast(it.message.toString())
                }

                else -> {
                }
            }
        }
    }

    /**
     * click event handel
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun initOnClick() {
        viewModel.onClick.observe(viewLifecycleOwner) {
            when (it?.id) {
                R.id.ivBackButton -> {
                    findNavController().popBackStack()
                }
            }

        }
    }

    /**
     * grade adapter
     */
    private fun initOnGradeAdapter() {
        gradeAdapter = SimpleRecyclerViewAdapter(R.layout.rv_grade_item, BR.bean) { v, m, _ ->
            when (v?.id) {
                R.id.clGrade -> {
                    if (!m._id.isNullOrEmpty() && !m.grade.isNullOrEmpty()) {
                        if (from=="home"){
                        val action = ChooseGradeFragmentDirections.navigateToPickSubjectFragment(
                            subjectId = m._id, grade = m.grade
                        )
                        BindingUtils.navigateWithSlide(findNavController(), action)
                    }
                        else{
                            val data = HashMap<String, Any>()
                            val language = sharedPrefManager.getLanguage()
                            data["preferredLanguage"] = language
                            data["grade"] = m.grade
                            data["gradeId"] = m._id
                            viewModel.updateProfileApi(Constants.UPDATE_PROFILE, data)
                        }

                    }
                }
            }
        }
        binding.rvGrade.adapter = gradeAdapter

    }

}
