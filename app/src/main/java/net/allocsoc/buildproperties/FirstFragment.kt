package net.allocsoc.buildproperties

import android.app.Activity
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import net.allocsoc.buildproperties.databinding.FragmentFirstBinding
import java.io.FileOutputStream
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.provider.DocumentsContract


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        return binding.root

    }
    fun openDirectory() {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            setType("*/*");

            putExtra(Intent.EXTRA_TITLE, "device.properties")
        }
        startActivityForResult(intent, 99)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.buttonFirst.setOnClickListener {
            openDirectory()
        }
    }

    override fun onActivityResult(
        requestCode: Int, resultCode: Int, resultData: Intent?) {
        if (requestCode == 99
            && resultCode == Activity.RESULT_OK) {

            resultData?.data?.also { uri ->
                getDeviceProperties(uri)
            }

        }
    }

    fun getDeviceProperties(uri: Uri) {
        val contentResolver = this.requireContext().applicationContext.contentResolver
        contentResolver.openFileDescriptor(uri, "w")?.use {
            FileOutputStream(it.fileDescriptor).use {
                NativeDeviceInfoProvider(this.requireContext())
                    .getNativeDeviceProperties()
                    .store(
                        it,
                        "DEVICE_CONFIG"
                    )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}