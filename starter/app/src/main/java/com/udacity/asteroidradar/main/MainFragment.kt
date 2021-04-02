package com.udacity.asteroidradar.main

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.R
import com.udacity.asteroidradar.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private lateinit var viewModel: MainViewModel
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentMainBinding.inflate(inflater)

        val viewModelFactory = MainViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val adapter = MainFragmentAdapter(AsteroidClickListener { asteroid ->
            findNavController().navigate(MainFragmentDirections.actionShowDetail(asteroid))
        })
        binding.asteroidRecycler.adapter = adapter

        binding.statusLoadingWheel.visibility = View.VISIBLE
        binding.asteroidRecycler.visibility = View.GONE

        viewModel.asteroidList.observe(viewLifecycleOwner, Observer { asteroids ->
            asteroids?.let {
                adapter.submitList(asteroids)
                binding.statusLoadingWheel.visibility = View.GONE
                binding.asteroidRecycler.visibility = View.VISIBLE
            }
        })

        viewModel.pictureOfDay.observe(viewLifecycleOwner, Observer { picture ->
            picture?.let {
                if (picture.mediaType == "image") {
                    setPictureOfDay(picture)
                }
            }
        })

        return binding.root
    }

    private fun setPictureOfDay(pictureOfDay: PictureOfDay) {
        Picasso.with(binding.activityMainImageOfTheDay.context)
            .load(pictureOfDay.url)
            .placeholder(R.drawable.loading_img)
            .error(R.drawable.ic_broken_image)
            .into(binding.activityMainImageOfTheDay)
    }
}
