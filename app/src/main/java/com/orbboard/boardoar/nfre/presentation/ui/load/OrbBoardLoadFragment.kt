package com.orbboard.boardoar.nfre.presentation.ui.load

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.orbboard.boardoar.MainActivity
import com.orbboard.boardoar.R
import com.orbboard.boardoar.databinding.FragmentLoadOrbBoardBinding
import com.orbboard.boardoar.nfre.data.shar.OrbBoardSharedPreference
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class OrbBoardLoadFragment : Fragment(R.layout.fragment_load_orb_board) {
    private lateinit var orbBoardLoadBinding: FragmentLoadOrbBoardBinding

    private val orbBoardLoadViewModel by viewModel<OrbBoardLoadViewModel>()

    private val orbBoardSharedPreference by inject<OrbBoardSharedPreference>()

    private var orbBoardUrl = ""

    private val orbBoardRequestNotificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        orbBoardSharedPreference.orbBoardNotificationState = 2
        orbBoardNavigateToSuccess(orbBoardUrl)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        orbBoardLoadBinding = FragmentLoadOrbBoardBinding.bind(view)

        orbBoardLoadBinding.orbBoardGrandButton.setOnClickListener {
            val orbBoardPermission = Manifest.permission.POST_NOTIFICATIONS
            orbBoardRequestNotificationPermission.launch(orbBoardPermission)
        }

        orbBoardLoadBinding.orbBoardSkipButton.setOnClickListener {
            orbBoardSharedPreference.orbBoardNotificationState = 1
            orbBoardSharedPreference.orbBoardNotificationRequest =
                (System.currentTimeMillis() / 1000) + 259200
            orbBoardNavigateToSuccess(orbBoardUrl)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                orbBoardLoadViewModel.orbBoardHomeScreenState.collect {
                    when (it) {
                        is OrbBoardLoadViewModel.OrbBoardHomeScreenState.OrbBoardLoading -> {

                        }

                        is OrbBoardLoadViewModel.OrbBoardHomeScreenState.OrbBoardError -> {
                            requireActivity().startActivity(
                                Intent(
                                    requireContext(),
                                    MainActivity::class.java
                                )
                            )
                            requireActivity().finish()
                        }

                        is OrbBoardLoadViewModel.OrbBoardHomeScreenState.OrbBoardSuccess -> {
                            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
                                val orbBoardNotificationState = orbBoardSharedPreference.orbBoardNotificationState
                                when (orbBoardNotificationState) {
                                    0 -> {
                                        orbBoardLoadBinding.orbBoardNotiGroup.visibility = View.VISIBLE
                                        orbBoardLoadBinding.orbBoardLoadingGroup.visibility = View.GONE
                                        orbBoardUrl = it.data
                                    }
                                    1 -> {
                                        if (System.currentTimeMillis() / 1000 > orbBoardSharedPreference.orbBoardNotificationRequest) {
                                            orbBoardLoadBinding.orbBoardNotiGroup.visibility = View.VISIBLE
                                            orbBoardLoadBinding.orbBoardLoadingGroup.visibility = View.GONE
                                            orbBoardUrl = it.data
                                        } else {
                                            orbBoardNavigateToSuccess(it.data)
                                        }
                                    }
                                    2 -> {
                                        orbBoardNavigateToSuccess(it.data)
                                    }
                                }
                            } else {
                                orbBoardNavigateToSuccess(it.data)
                            }
                        }

                        OrbBoardLoadViewModel.OrbBoardHomeScreenState.OrbBoardNotInternet -> {
                            orbBoardLoadBinding.orbBoardStateGroup.visibility = View.VISIBLE
                            orbBoardLoadBinding.orbBoardLoadingGroup.visibility = View.GONE
                        }
                    }
                }
            }
        }
    }


    private fun orbBoardNavigateToSuccess(data: String) {
        findNavController().navigate(
            R.id.action_orbBoardLoadFragment_to_orbBoardV,
            bundleOf(ORB_BOARD_D to data)
        )
    }

    companion object {
        const val ORB_BOARD_D = "orbBoardData"
    }
}