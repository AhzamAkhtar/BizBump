package com.example.android.google_sol.login.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.android.google_sol.R
import com.example.android.google_sol.databinding.FragmentRegistrationBinding
import com.example.android.google_sol.login.util.LoginViewModal
import java.util.HashMap

class FragmentSignup: Fragment() {
    private val binding by lazy { FragmentRegistrationBinding.inflate(layoutInflater) }
    private val viewModel : LoginViewModal by activityViewModels()
    private var hashMap: HashMap<String, String> = HashMap<String, String>()
    private var hashMapDOB: HashMap<String, Int> = HashMap<String, Int>()

    private var date: String? = null
    private var month: String? = null
    private var year: String? = null
    private var gender: String? = null
    private var country: String? = null
    private var dateInt: Int? = null
    private var monthInt: Int? = null
    private var yearInt: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvLogin.setOnClickListener{
            viewModel.setScreenState(LoginActivity.LOGIN_SCREEN)
        }

        binding.tvErrorName.visibility = View.GONE
        binding.tvErrorPhoneumber.visibility = View.GONE
        binding.tvErrorDOB.visibility = View.GONE
        binding.tvErrorGender.visibility = View.GONE
        binding.tvErrorCountryCity.visibility =View.GONE

        setUpAllDropdowns()
        selectValuesFromDropdowns()

    }

    private fun setUpAllDropdowns(){
        //binding.registrationHeader.headerText.text = "Register"
        //binding.registrationHeader.image.visibility = View.GONE
        val date = resources.getStringArray(R.array.date)
        val arrayAdapterDate = context?.let { ArrayAdapter(it, R.layout.dropdown_layout, date) }
        binding.selectDate.setAdapter(arrayAdapterDate)
        val month = resources.getStringArray(R.array.month)
        val arrayAdapterMonth = context?.let { ArrayAdapter(it, R.layout.dropdown_layout, month) }
        binding.selectMonth.setAdapter(arrayAdapterMonth)
        val gender = resources.getStringArray(R.array.gender)
        val arrayAdapterGender = context?.let { ArrayAdapter(it, R.layout.dropdown_layout, gender) }
        binding.actSelectGender.setAdapter(arrayAdapterGender)
        val country = resources.getStringArray(R.array.countries_array)
        val arrayAdapterCountry =
            context?.let { ArrayAdapter(it, R.layout.dropdown_layout, country) }
        binding.selectCountryd.setAdapter(arrayAdapterCountry)
        val years = resources.getStringArray(R.array.years)
        val arrayAdapterYears = context?.let { ArrayAdapter(it, R.layout.dropdown_layout, years) }
        binding.selectYear.setAdapter(arrayAdapterYears)
    }

    private fun selectValuesFromDropdowns() {
        binding.selectDate.setOnItemClickListener { parent, _, position, _ ->
            this.date =
                (parent.getItemAtPosition(position) as String?) ?: return@setOnItemClickListener
            Toast.makeText(context, date.toString(), Toast.LENGTH_LONG).show()
            hashMapDOB["date"] = this.date!!.toInt()
        }
        binding.selectMonth.setOnItemClickListener { parent, _, position, _ ->
            this.month =
                (parent.getItemAtPosition(position) as String?) ?: return@setOnItemClickListener
            //val tt = UtilForMonth.returnMonthNumber(this.month.toString())
            //Toast.makeText(context, tt.toString(), Toast.LENGTH_LONG).show()
            //hashMapDOB["month"] = tt!!.toInt()
        }
        binding.actSelectGender.setOnItemClickListener { parent, _, position, _ ->
            this.gender =
                (parent.getItemAtPosition(position) as String?) ?: return@setOnItemClickListener
            hashMap["gender"] = this.gender!!
        }
        binding.selectCountryd.setOnItemClickListener { parent, _, position, _ ->
            this.country =
                (parent.getItemAtPosition(position) as String?) ?: return@setOnItemClickListener
            hashMap["country"] = this.country!!
        }
        binding.selectYear.setOnItemClickListener { parent, _, position, _ ->
            this.year =
                (parent.getItemAtPosition(position) as String?) ?: return@setOnItemClickListener
            hashMapDOB["year"] = year!!.toInt()
        }
//        binding.edphoneNumber.setText(viewModel.phoneNumberRegistration.value)
    }
}