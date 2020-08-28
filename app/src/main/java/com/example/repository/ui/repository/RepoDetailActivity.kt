package com.example.repository.ui.repository

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.repository.R
import com.example.repository.common.constant.Constants
import com.example.repository.common.di.ComponentsProvider
import com.example.repository.common.network.Status
import com.example.repository.common.utils.AppUtils
import com.example.repository.common.utils.AppUtils.fromHtml
import com.example.repository.common.utils.AppUtils.showSnackBar
import com.example.repository.common.utils.ImageLoader
import kotlinx.android.synthetic.main.activity_repo_detail.*
import javax.inject.Inject

class RepoDetailActivity : AppCompatActivity() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val repositoryViewModel: RepositoryViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(RepositoryViewModel::class.java)
    }
    private var comments: String? = ""
    /* @Inject
     lateinit var picasso: Picasso*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repo_detail)
        ComponentsProvider.getComponent().inject(this)
        title = intent?.extras?.getString(Constants.NAME)?.capitalize() ?: "Repository"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val id = intent?.extras?.getString(Constants.ID)
        comments = intent?.extras?.getString(Constants.COMMENT)
        id?.let { fetchRepoDetail(it) }
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return super.onNavigateUp()
    }

    private fun fetchRepoDetail(id: String) {
        if (!AppUtils.isConnectingToInternet(this)) {
            showSnackBar(container)
            container.visibility = View.GONE
            return
        }
            repositoryViewModel.getRepoDetails(id).observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    it.data?.let { detail ->
                        container.visibility = View.VISIBLE
                        default_branch.text = detail.default_branch
                        fork.text = fromHtml("<b>" + detail.forks + "</b>".plus(" Forks"))
                        watchers.text = fromHtml("<b>" + detail.watchers + "</b>".plus(" Watchers"))
                        stargazers_count.text =
                            fromHtml("<b>" + detail.stargazers_count + "</b>".plus(" Stargazers"))
                        open_issues_count.text =
                            fromHtml("<b>" + detail.open_issues_count + "</b>".plus(" Open issues"))
                        subscribers_count.text =
                            fromHtml("<b>" + detail.subscribers_count + "</b>".plus(" Subscribers"))
                        size.text =
                            fromHtml("Size:".plus("<b>" + detail.subscribers_count) + "</b>")
                        tv_id.text = detail.nodeId
                        ll_detail.visibility = View.VISIBLE
                        val imgLoader = ImageLoader()
                        detail.owner?.avatarUrl?.let {
                            imgLoader.DisplayImage(
                                it,
                                R.drawable.ic_loading,
                                iv_avatar
                            )
                        }

                        //  picasso.load(detail.owner?.avatarUrl).fit().into(iv_avatar)
                        tv_name.text = detail.name?.capitalize()
                        tv_desc.text = detail.description
                        tv_repo_url.text = detail.owner?.reposUrl
                        tv_user_comments.text = comments
                        tv_user_type.text = detail.owner?.type
                        pb_loading_detail.visibility = View.GONE
                    }

                }
                Status.ERROR -> {
                    pb_loading_detail.visibility = View.GONE
                }
                Status.LOADING -> {
                    pb_loading_detail.visibility = View.VISIBLE
                }
            }
        })
    }
}
