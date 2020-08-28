package com.example.repository.ui.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.repository.R
import com.example.repository.common.di.ComponentsProvider
import com.example.repository.common.network.Status
import com.example.repository.common.utils.AppUtils
import com.example.repository.common.utils.AppUtils.showSnackBar
import com.example.repository.models.Repository
import kotlinx.android.synthetic.main.fragment_home.*
import timber.log.Timber
import javax.inject.Inject

class RepoListFragment : Fragment() {

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    /* @Inject
     lateinit var picasso: Picasso*/

    lateinit var adapter: RepoAdapter

    private val repositoryViewModel: RepositoryViewModel by lazy {
        ViewModelProvider(
            this.requireActivity(),
            viewModelFactory
        ).get(RepositoryViewModel::class.java)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        ComponentsProvider.getComponent().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setLayoutManager()
    }

    override fun onStart() {
        super.onStart()
        if (!checkPermissions()) {
            requestPermissions()
        } else {
            fetchRepoList()
        }
    }

    private fun setLayoutManager() {
        val mLayoutManager = androidx.recyclerview.widget.LinearLayoutManager(this.requireContext())
        mLayoutManager.orientation = RecyclerView.VERTICAL
        recycler_view.layoutManager = mLayoutManager
        recycler_view.setHasFixedSize(true)
    }

    private fun fetchRepoList() {
        if (AppUtils.isConnectingToInternet(this.requireActivity())) {
            repositoryViewModel.getRepoList().observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.SUCCESS -> {
                        it.data?.let { data ->
                            pb_loading.visibility = View.GONE
                            if (data.isNotEmpty()) {
                                Timber.d("success$data")
                                recycler_view.visibility = View.VISIBLE
                                tv_no_data.visibility = View.GONE
                                setAdapter(data)
                            } else {
                                tv_no_data.visibility = View.VISIBLE
                                recycler_view.visibility = View.GONE
                            }

                        }
                    }
                    Status.ERROR -> {
                        pb_loading.visibility = View.GONE
                        Toast.makeText(
                            activity, "Some thing went wrong. Please try again",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    Status.LOADING -> {
                        pb_loading.visibility = View.VISIBLE
                    }
                }
            })
        } else {
            showSnackBar(container)
        }

    }

    private fun setAdapter(data: List<Repository>) {
        adapter = RepoAdapter(data)
        adapter.notifyDataSetChanged()
        recycler_view.adapter = adapter
    }

    private fun checkPermissions(): Boolean {
        val permissionState = ActivityCompat.checkSelfPermission(
            this.requireActivity(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return permissionState == PackageManager.PERMISSION_GRANTED
    }

    private fun startPermissionRequest() {
        requestPermissions(
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            REQUEST_PERMISSIONS_REQUEST_CODE
        )
    }

    private fun requestPermissions() {
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this.requireActivity(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Toast.makeText(
                this.requireActivity(), R.string.permission_rationale,
                Toast.LENGTH_LONG
            ).show()

        } else {
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startPermissionRequest()
        }
    }

    /**
     * Callback received when a permissions request has been completed.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            when {
                grantResults.isEmpty() -> {
                    // If user interaction was interrupted, the permission request is cancelled and you
                    // receive empty arrays.
                }
                grantResults[0] == PackageManager.PERMISSION_GRANTED -> // Permission granted.
                    fetchRepoList()
                else -> // Permission denied.

                    // Notify the user via a SnackBar that they have rejected a core permission for the
                    // app, which makes the Activity useless. In a real app, core permissions would
                    // typically be best requested during a welcome-screen flow.

                    // Additionally, it is important to remember that a permission might have been
                    // rejected without asking the user for permission (device policy or "Never ask
                    // again" prompts). Therefore, a user interface affordance is typically implemented
                    // when permissions are denied. Otherwise, your app could appear unresponsive to
                    // touches or interactions which have required permissions.
                    Toast.makeText(
                        this.requireActivity(), R.string.permission_denied_explanation,
                        Toast.LENGTH_LONG
                    ).show()
            }
        }

    }
}