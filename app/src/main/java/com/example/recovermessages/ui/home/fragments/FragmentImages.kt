package com.example.recovermessages.ui.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recovermessages.databinding.FragmentImagesBinding
import com.example.recovermessages.models.ModelFiles
import com.example.recovermessages.ui.home.adapters.FilesAdapter
import com.example.recovermessages.ui.home.viewmodel.ViewModelImages
import com.example.recovermessages.utils.AppUtils.hide
import com.example.recovermessages.utils.AppUtils.show
import timber.log.Timber

class FragmentImages : Fragment() {
    private lateinit var binding: FragmentImagesBinding
    private val viewModel: ViewModelImages by viewModels()
    private var pack: String? = null

    private var filesAdapter: FilesAdapter? = null
    private var filesList: MutableList<ModelFiles> = mutableListOf()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pack = requireArguments().getString("pack")
    }


    fun newInstance(str: String?): FragmentImages {
        val fragmentImages = FragmentImages()
        val bundle = Bundle()
        bundle.putString("pack", str)
        fragmentImages.arguments = bundle
        return fragmentImages
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImagesBinding.inflate(inflater, container, false)

        viewModel.exectueProcess()

        binding.progressbar.show()
        viewModel.inputAudioDataSet.observe(requireActivity()) {
            filesList.clear()
            filesList.addAll(it)

            Timber.d("fileList size is ${filesList.size}")


            binding.progressbar.hide()



            filesAdapter = FilesAdapter(requireContext(), filesList)

            binding.imagesRecycler.layoutManager = GridLayoutManager(requireActivity(), 2)
            binding.imagesRecycler.adapter = filesAdapter

            if (filesList.isNotEmpty()) {
                binding.notfound.hide()
                binding.imagesRecycler.show()
            } else {
                binding.notfound.show()
                binding.imagesRecycler.hide()
            }


        }

        return binding.root
    }
}