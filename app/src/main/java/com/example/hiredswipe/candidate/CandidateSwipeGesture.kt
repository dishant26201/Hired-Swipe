package com.example.hiredswipe.candidate

import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.coroutines.coroutineContext

abstract class CandidateSwipeGesture : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) { }
}