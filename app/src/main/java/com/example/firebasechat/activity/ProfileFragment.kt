package com.example.firebasechat.activity

import android.app.Activity.RESULT_OK
import android.content.Intent
import com.example.firebasechat.activity.LoginActivity  // Import the LoginActivity class if not already imported.


import android.app.ProgressDialog
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.firebasechat.R
import com.example.firebasechat.databinding.FragmentProfileBinding
import com.example.firebasechat.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.*
private const val PICK_IMAGE_REQUEST: Int = 2020
private const val STORAGE_IMAGE_PATH = "images/"


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference
    private var filePath: Uri? = null
    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment using ViewBinding
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)

        storage = FirebaseStorage.getInstance()
        storageRef = storage.reference

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)
                if (user != null) {
                    binding.etUserName.setText(user.userName)

                    if (user.profileImage == "") {
                        binding.userImage.setImageResource(R.drawable.profile_image)
                    } else {
                        Glide.with(this@ProfileFragment).load(user.profileImage).into(binding.userImage)
                    }
                }
            }

        })

        binding.userImage.setOnClickListener {
            chooseImage()
        }

        binding.btnSave.setOnClickListener {
            uploadImage()
            binding.progressBar.visibility = View.VISIBLE
        }
        binding.logout.setOnClickListener {
            FirebaseAuth.getInstance().signOut() // Sign out the user
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }


    }


    private fun chooseImage() {
        val intent: Intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            filePath = data!!.data
            try {
                var bitmap: Bitmap =
                    MediaStore.Images.Media.getBitmap(requireContext().contentResolver, filePath)
                binding.userImage.setImageBitmap(bitmap)
                binding.btnSave.visibility = View.VISIBLE
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage() {
        if (filePath != null) {
            val ref: StorageReference =
                storageRef.child(STORAGE_IMAGE_PATH + UUID.randomUUID().toString())

            val uploadTask = ref.putFile(filePath!!)
            uploadTask.addOnSuccessListener { taskSnapshot ->
                // Get the download URL and update the user's profile in the Realtime Database
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                    val hashMap = HashMap<String, Any>()
                    hashMap["userName"] = binding.etUserName.text.toString()
                    hashMap["profileImage"] = uri.toString()
                    databaseReference.updateChildren(hashMap)
                    // Dismiss the progress bar
                    binding.progressBar.visibility = View.GONE
                    // Show a success message
                    Toast.makeText(
                        requireContext(),
                        "Uploaded",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Hide the Save button
                    binding.btnSave.visibility = View.GONE
                }
            }.addOnFailureListener { exception ->
                // Handle upload failure and show an error message
                binding.progressBar.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Failed: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}