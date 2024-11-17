package com.example.fitnessapp.interfaces

/** This interface is used to define onRefreshListener method, which is going to be implemented
 *  by the panels, in order to re-populate the panel, as viewPager.setCurrentItem() does not trigger
 *  onResume() method, which is usually used re-populate the panel
 * */
interface IFragmentRefreshListener {
    fun onRefreshListener()
}