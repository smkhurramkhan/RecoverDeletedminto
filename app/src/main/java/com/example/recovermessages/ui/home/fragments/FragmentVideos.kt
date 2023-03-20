package com.example.recovermessages.ui.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recovermessages.databinding.FragmentVideosBinding
import com.example.recovermessages.models.ModelFiles
import com.example.recovermessages.ui.home.adapters.FilesAdapter
import com.example.recovermessages.ui.home.viewmodel.ViewModelVideos
import com.example.recovermessages.utils.AppUtils.hide
import com.example.recovermessages.utils.AppUtils.show
import timber.log.Timber

class FragmentVideos : Fragment() {
    private lateinit var binding: FragmentVideosBinding
    private val viewModel: ViewModelVideos by viewModels()
    private var pack: String? = null

    private var filesAdapter: FilesAdapter? = null
    private var filesList: MutableList<ModelFiles> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pack = requireArguments().getString("pack")
    }


    fun newInstance(str: String?): FragmentVideos {
        val fragmentVideos = FragmentVideos()
        val bundle = Bundle()
        bundle.putString("pack", str)
        fragmentVideos.arguments = bundle
        return fragmentVideos
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentVideosBinding.inflate(inflater, container, false)

        viewModel.exectueProcess()

        binding.progressbar.show()

        viewModel.inputAudioDataSet.observe(requireActivity()) {
            filesList.clear()
            filesList.addAll(it)

            Timber.d("file list size is ${filesList.size}")


            binding.progressbar.hide()


            filesAdapter = FilesAdapter(requireContext(), filesList)

            binding.videosRecycler.layoutManager = GridLayoutManager(requireActivity(), 2)
            binding.videosRecycler.adapter = filesAdapter

            if (filesList.isNotEmpty()) {
                binding.notfound.hide()
                binding.videosRecycler.show()
            } else {
                binding.notfound.show()
                binding.videosRecycler.hide()
            }


        }

        return binding.root
    }
}