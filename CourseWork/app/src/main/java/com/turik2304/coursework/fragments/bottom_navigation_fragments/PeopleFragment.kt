package com.turik2304.coursework.fragments.bottom_navigation_fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.turik2304.coursework.*
import com.turik2304.coursework.network.FakeServerApi
import com.turik2304.coursework.network.LoadersID
import com.turik2304.coursework.network.ServerApi
import com.turik2304.coursework.recycler_view_base.AsyncAdapter
import com.turik2304.coursework.recycler_view_base.DiffCallback
import com.turik2304.coursework.recycler_view_base.ViewTyped
import com.turik2304.coursework.recycler_view_base.holder_factories.MainHolderFactory
import com.turik2304.coursework.recycler_view_base.items.UserUI
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

class PeopleFragment : Fragment() {

    private lateinit var innerViewTypedList: List<ViewTyped>
    private lateinit var asyncAdapter: AsyncAdapter<ViewTyped>
    private val api: ServerApi = FakeServerApi()
    private var positionOfClickedView = -1
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_people, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val usersToolbarShimmer = view.findViewById<ShimmerFrameLayout>(R.id.usersShimmer)
        usersToolbarShimmer.startShimmer()
        val recyclerViewUsers = view.findViewById<RecyclerView>(R.id.recycleViewUsers)

        val clickListener = { clickedView: View ->
            val userShimmer = clickedView as ShimmerFrameLayout
            userShimmer.showShimmer(true)
            positionOfClickedView =
                recyclerViewUsers.getChildAdapterPosition(clickedView)
            val clickedUserUI = asyncAdapter.items.currentList[positionOfClickedView] as UserUI
            loadProfileDetails(clickedUserUI, requireActivity())
        }
        val editText = view.findViewById<EditText>(R.id.edSearchUsers)
        val holderFactory = MainHolderFactory(clickListener)
        val diffCallBack = DiffCallback<ViewTyped>()

        asyncAdapter = AsyncAdapter(holderFactory, diffCallBack)
        recyclerViewUsers.adapter = asyncAdapter
        compositeDisposable.add(
            api.getUserUIListFromServer(requireActivity(), LoadersID.PEOPLE_LOADER_ID)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { userList ->
                        asyncAdapter.items.submitList(userList)
                        innerViewTypedList = userList
                        Search.initSearch(
                            editText,
                            innerViewTypedList,
                            asyncAdapter,
                            recyclerViewUsers
                        )
                        usersToolbarShimmer.stopAndHideShimmer()
                        checkSavedInstanceState(savedInstanceState)
                    },
                    { onError ->
                        Error.showError(
                            context,
                            onError
                        )
                        usersToolbarShimmer.stopAndHideShimmer()
                    })
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(BUNDLE_POSITION_OF_CLICKED_VIEW, positionOfClickedView)
    }

    private fun checkSavedInstanceState(bundle: Bundle?) {
        if (bundle != null) {
            positionOfClickedView = bundle.getInt(BUNDLE_POSITION_OF_CLICKED_VIEW)
            if (positionOfClickedView != -1) {
                val clickedUserUI = asyncAdapter.items.currentList[positionOfClickedView] as UserUI
                loadProfileDetails(clickedUserUI, requireActivity())
            }
        }
    }

    private fun startProfileDetailsFragment(userName: String, statusText: String, status: String) {
        parentFragmentManager.beginTransaction()
            .replace(
                R.id.fragmentContainer,
                ProfileDetailsFragment.newInstance(
                    userName,
                    statusText,
                    status
                )
            )
            .addToBackStack(null)
            .commit()
    }

    private fun loadProfileDetails(
        userUI: UserUI,
        activity: FragmentActivity,
    ) {
        userUI.profileDetailsLoadingStarted = true
        val email = userUI.email
        val name = userUI.userName
        compositeDisposable.add(
            api.getProfileDetailsById(email, activity)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { status ->
                        startProfileDetailsFragment(name, status, status)
                        userUI.profileDetailsLoadingStarted = false
                        positionOfClickedView = -1
                    },
                    { onError ->
                        Error.showError(
                            context,
                            onError
                        )
                        userUI.profileDetailsLoadingStarted = false
                        positionOfClickedView = -1
                    })
        )
    }

    override fun onStop() {
        super.onStop()
        if (positionOfClickedView != -1) {
            (asyncAdapter.items.currentList[positionOfClickedView] as UserUI)
                .profileDetailsLoadingStarted = false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    companion object {
        const val BUNDLE_POSITION_OF_CLICKED_VIEW = "POSITION"
    }

}

