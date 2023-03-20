package com.example.recovermessages.ui.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.recovermessages.databinding.FragmentDocsBinding
import com.example.recovermessages.models.ModelFiles
import com.example.recovermessages.ui.home.adapters.FilesAdapter
import com.example.recovermessages.ui.home.viewmodel.ViewModelDocs
import com.example.recovermessages.utils.AppUtils.hide
import com.example.recovermessages.utils.AppUtils.show
import timber.log.Timber

class FragmentDocs : Fragment() {

    private lateinit var binding: FragmentDocsBinding
    private val viewModel: ViewModelDocs by viewModels()
    private var pack: String? = null

    private var filesAdapter: FilesAdapter? = null
    private var filesList: MutableList<ModelFiles> = mutableListOf()


    fun newInstance(str: String?): FragmentDocs {
        val fragmentDocs = FragmentDocs()
        val bundle = Bundle()
        bundle.putString("pack", str)
        fragmentDocs.arguments = bundle
        return fragmentDocs
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pack = requireArguments().getString("pack")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDocsBinding.inflate(inflater, container, false)

        viewModel.executeProcess()

        binding.progressbar.show()

        viewModel.inputAudioDataSet.observe(requireActivity()) {
            filesList.clear()
            filesList.addAll(it)

            Timber.d("fileList size is ${filesList.size}")


            binding.progressbar.hide()


            filesAdapter = FilesAdapter(requireContext(), filesList)

            binding.docsRecycler.layoutManager = GridLayoutManager(requireActivity(), 2)
            binding.docsRecycler.adapter = filesAdapter

            if (filesList.isNotEmpty()) {
                binding.notfound.hide()
                binding.docsRecycler.show()
            } else {
                binding.notfound.show()
                binding.docsRecycler.hide()
            }


        }

        return binding.root
    }
}