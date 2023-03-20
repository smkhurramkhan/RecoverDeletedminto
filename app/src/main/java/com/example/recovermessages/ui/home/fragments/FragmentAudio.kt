package com.example.recovermessages.ui.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recovermessages.ui.home.adapters.FilesAdapter
import com.example.recovermessages.databinding.FragmentAudioBinding
import com.example.recovermessages.models.ModelFiles
import com.example.recovermessages.ui.home.viewmodel.VoiceViewModel
import com.example.recovermessages.utils.AppUtils.hide
import com.example.recovermessages.utils.AppUtils.show
import timber.log.Timber

class FragmentAudio : Fragment() {
    private lateinit var binding: FragmentAudioBinding
    private val viewModel: VoiceViewModel by viewModels()
    private var pack: String? = null

    private var filesAdapter: FilesAdapter? = null
    private var filesList: MutableList<ModelFiles> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pack = requireArguments().getString("pack")
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAudioBinding.inflate(inflater, container, false)

        viewModel.exectueProcess()

        binding.progressbar.show()
        viewModel.inputAudioDataSet.observe(requireActivity()) {
            filesList.clear()
            filesList.addAll(it)

            Timber.d("file list size is ${filesList.size}")


            binding.progressbar.hide()


            filesAdapter = FilesAdapter(requireContext(), filesList)

            binding.audioRecycler.layoutManager = GridLayoutManager(requireActivity(), 2)
            binding.audioRecycler.adapter = filesAdapter

            if (filesList.isNotEmpty()) {
                binding.notfound.hide()
                binding.audioRecycler.show()
            } else {
                binding.notfound.show()
                binding.audioRecycler.hide()
            }


        }

        return binding.root
    }
}