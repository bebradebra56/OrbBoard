package com.orbboard.boardoar.nfre.presentation.ui.view

import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.orbboard.boardoar.nfre.presentation.app.OrbBoardApplication
import com.orbboard.boardoar.nfre.presentation.ui.load.OrbBoardLoadFragment
import org.koin.android.ext.android.inject

class OrbBoardV : Fragment(){

    private lateinit var orbBoardPhoto: Uri
    private var orbBoardFilePathFromChrome: ValueCallback<Array<Uri>>? = null

    private val orbBoardTakeFile: ActivityResultLauncher<PickVisualMediaRequest> = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        orbBoardFilePathFromChrome?.onReceiveValue(arrayOf(it ?: Uri.EMPTY))
        orbBoardFilePathFromChrome = null
    }

    private val orbBoardTakePhoto: ActivityResultLauncher<Uri> = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        if (it) {
            orbBoardFilePathFromChrome?.onReceiveValue(arrayOf(orbBoardPhoto))
            orbBoardFilePathFromChrome = null
        } else {
            orbBoardFilePathFromChrome?.onReceiveValue(null)
            orbBoardFilePathFromChrome = null
        }
    }

    private val orbBoardDataStore by activityViewModels<OrbBoardDataStore>()


    private val orbBoardViFun by inject<OrbBoardViFun>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "Fragment onCreate")
        CookieManager.getInstance().setAcceptCookie(true)
        requireActivity().onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (orbBoardDataStore.orbBoardView.canGoBack()) {
                        orbBoardDataStore.orbBoardView.goBack()
                        Log.d(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "WebView can go back")
                    } else if (orbBoardDataStore.orbBoardViList.size > 1) {
                        Log.d(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "WebView can`t go back")
                        orbBoardDataStore.orbBoardViList.removeAt(orbBoardDataStore.orbBoardViList.lastIndex)
                        Log.d(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "WebView list size ${orbBoardDataStore.orbBoardViList.size}")
                        orbBoardDataStore.orbBoardView.destroy()
                        val previousWebView = orbBoardDataStore.orbBoardViList.last()
                        orbBoardAttachWebViewToContainer(previousWebView)
                        orbBoardDataStore.orbBoardView = previousWebView
                    }
                }

            })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (orbBoardDataStore.orbBoardIsFirstCreate) {
            orbBoardDataStore.orbBoardIsFirstCreate = false
            orbBoardDataStore.orbBoardContainerView = FrameLayout(requireContext()).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                id = View.generateViewId()
            }
            return orbBoardDataStore.orbBoardContainerView
        } else {
            return orbBoardDataStore.orbBoardContainerView
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "onViewCreated")
        if (orbBoardDataStore.orbBoardViList.isEmpty()) {
            orbBoardDataStore.orbBoardView = OrbBoardVi(requireContext(), object :
                OrbBoardCallBack {
                override fun orbBoardHandleCreateWebWindowRequest(orbBoardVi: OrbBoardVi) {
                    orbBoardDataStore.orbBoardViList.add(orbBoardVi)
                    Log.d(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "WebView list size = ${orbBoardDataStore.orbBoardViList.size}")
                    Log.d(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "CreateWebWindowRequest")
                    orbBoardDataStore.orbBoardView = orbBoardVi
                    orbBoardVi.orbBoardSetFileChooserHandler { callback ->
                        orbBoardHandleFileChooser(callback)
                    }
                    orbBoardAttachWebViewToContainer(orbBoardVi)
                }

            }, orbBoardWindow = requireActivity().window).apply {
                orbBoardSetFileChooserHandler { callback ->
                    orbBoardHandleFileChooser(callback)
                }
            }
            orbBoardDataStore.orbBoardView.orbBoardFLoad(arguments?.getString(
                OrbBoardLoadFragment.ORB_BOARD_D) ?: "")
//            ejvview.fLoad("www.google.com")
            orbBoardDataStore.orbBoardViList.add(orbBoardDataStore.orbBoardView)
            orbBoardAttachWebViewToContainer(orbBoardDataStore.orbBoardView)
        } else {
            orbBoardDataStore.orbBoardViList.forEach { webView ->
                webView.orbBoardSetFileChooserHandler { callback ->
                    orbBoardHandleFileChooser(callback)
                }
            }
            orbBoardDataStore.orbBoardView = orbBoardDataStore.orbBoardViList.last()

            orbBoardAttachWebViewToContainer(orbBoardDataStore.orbBoardView)
        }
        Log.d(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "WebView list size = ${orbBoardDataStore.orbBoardViList.size}")
    }

    private fun orbBoardHandleFileChooser(callback: ValueCallback<Array<Uri>>?) {
        Log.d(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "handleFileChooser called, callback: ${callback != null}")

        orbBoardFilePathFromChrome = callback

        val listItems: Array<out String> = arrayOf("Select from file", "To make a photo")
        val listener = DialogInterface.OnClickListener { _, which ->
            when (which) {
                0 -> {
                    Log.d(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "Launching file picker")
                    orbBoardTakeFile.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }
                1 -> {
                    Log.d(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "Launching camera")
                    orbBoardPhoto = orbBoardViFun.orbBoardSavePhoto()
                    orbBoardTakePhoto.launch(orbBoardPhoto)
                }
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Choose a method")
            .setItems(listItems, listener)
            .setCancelable(true)
            .setOnCancelListener {
                Log.d(OrbBoardApplication.ORB_BOARD_MAIN_TAG, "File chooser canceled")
                callback?.onReceiveValue(null)
                orbBoardFilePathFromChrome = null
            }
            .create()
            .show()
    }

    private fun orbBoardAttachWebViewToContainer(w: OrbBoardVi) {
        orbBoardDataStore.orbBoardContainerView.post {
            (w.parent as? ViewGroup)?.removeView(w)
            orbBoardDataStore.orbBoardContainerView.removeAllViews()
            orbBoardDataStore.orbBoardContainerView.addView(w)
        }
    }


}