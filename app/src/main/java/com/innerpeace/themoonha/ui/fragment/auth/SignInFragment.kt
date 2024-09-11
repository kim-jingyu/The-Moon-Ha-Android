package com.innerpeace.themoonha.ui.fragment.auth

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.innerpeace.themoonha.R
import com.innerpeace.themoonha.SharedPreferencesManager
import com.innerpeace.themoonha.data.model.auth.LoginRequest
import com.innerpeace.themoonha.data.network.ApiClient
import com.innerpeace.themoonha.data.network.AuthService
import com.innerpeace.themoonha.data.repository.AuthRepository
import com.innerpeace.themoonha.databinding.FragmentSignInBinding
import kotlinx.coroutines.launch

class SignInFragment : Fragment() {
    private var mBinding : FragmentSignInBinding? = null
    private val binding get() = mBinding!!
    private val authRepository = AuthRepository(ApiClient.getClient().create(AuthService::class.java))
    private lateinit var sharedPreferencesManager: SharedPreferencesManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentSignInBinding.inflate(inflater, container, false)
        sharedPreferencesManager = SharedPreferencesManager(requireContext().applicationContext)
        mBinding = binding
        return mBinding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.signInButton.setOnClickListener {
            val loginId = binding.inputLoginId.text.toString()
            val password = binding.inputPassword.text.toString()

            if (loginId.isNotEmpty() && password.isNotEmpty()) {
                login(loginId, password)
            } else {
                Toast.makeText(requireContext(), "아이디와 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun login(loginId: String, password: String) {
        lifecycleScope.launch {
            val loginRequest = LoginRequest(username = loginId, password = password)
            val response = authRepository.fetchLogin(loginRequest)

            if (response != null && response.success) {
                sharedPreferencesManager.setIsLogin(true)
                Log.i("shared : ", sharedPreferencesManager.getIsLogin().toString())
                Log.i("shared : ", sharedPreferencesManager.getLoginToken().toString())
                findNavController().navigate(R.id.action_signInFragment_to_fragment_lesson)
            } else {
                Toast.makeText(requireContext(), "로그인 실패", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}