package com.example.listtodoapp.home

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.*
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listtodoapp.MainActivity
import com.example.listtodoapp.Model.Task
import com.example.listtodoapp.R
import com.example.listtodoapp.databinding.FragmentHomeBinding
import com.example.listtodoapp.sharedPref.SPApp
import com.google.android.datatransport.runtime.firebase.transport.LogEventDropped
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.DateFormat
import java.util.*


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var navController: NavController
    val TAG = "HomeFragment"
    private var taskList = mutableListOf<Task>()
    var uid = ""
    private val homeAdapter by lazy { HomeAdapter() }
    var firebaseDatabase: FirebaseDatabase? = null
    var databaseReference: DatabaseReference? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = view.findNavController()


        binding.addTaskBtn.setOnClickListener {

            if (findNavController().currentDestination?.id == R.id.HomeFragment)
                navController.navigate(R.id.action_homeFragment_to_taskFragment)

        }
        initRV()
        getTasksFromFireBase()
        initSwipe()


    }


    private fun showNotification(title: String?, body: String?, cont: Context) {

        val intent = Intent(cont, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            cont,
            111,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notificationBuilder = NotificationCompat.Builder(cont)
            .setSmallIcon(R.drawable.ic_access_time_black_24dp)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(2)
        Log.d(TAG, "ss ${context!!}")
        val notificationManager =
            context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(1, notificationBuilder.build())


    }

    private fun getTasksFromFireBase() {
        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase!!.getReference("TaskToDo")

        val sp = SPApp(requireContext())
        uid = sp!!.uid

        databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val value =
                        snapshot.child(postSnapshot.key!!).child(uid)
                            .getValue(Task::class.java)
                    val currentDateTimeString: String = DateFormat.getDateInstance().format(Date())

                    if(value!!.date.toString()==currentDateTimeString)
                    {
                        showNotification(value.title, value.description,requireContext())

                    }
                    taskList.add(value!!)
                    homeAdapter.submitList(taskList.distinct())

                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Fail to get data $error", Toast.LENGTH_SHORT)
                    .show()
            }


        })


    }

    private fun initRV() {
        binding.todoRv.adapter = homeAdapter
        binding.todoRv.layoutManager = LinearLayoutManager(activity)


    }


    private fun initSwipe() {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                firebaseDatabase = FirebaseDatabase.getInstance()
                databaseReference = firebaseDatabase!!.getReference("TaskToDo")

                if (direction == ItemTouchHelper.LEFT) {
                    databaseReference!!.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            for (postSnapshot in snapshot.children) {
                                val value =
                                    snapshot.child(postSnapshot.key!!).child(uid)
                                        .getValue(Task::class.java)
                                if (taskList[position].title == value!!.title) {

                                    val applesQuery: Query = databaseReference!!
                                        .child(postSnapshot.key!!).child(uid)
                                    applesQuery.ref.removeValue()

                                    taskList.removeAt(position)
                                    homeAdapter.notifyItemRemoved(position)
                                }


                            }


                        }


                        override fun onCancelled(error: DatabaseError) {
                            Toast.makeText(
                                requireContext(),
                                "Fail to delete data $error",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }


                    })


                }
            }

            override fun onChildDraw(
                canvas: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val itemView = viewHolder.itemView

                    val paint = Paint()
                    val icon: Bitmap

                    if (dX < 0) {

                        icon = BitmapFactory.decodeResource(resources, R.mipmap.ic_delete_white_png)
                        paint.color = Color.parseColor("#D32F2F")

                        canvas.drawRect(
                            itemView.right.toFloat() + dX, itemView.top.toFloat(),
                            itemView.right.toFloat(), itemView.bottom.toFloat(), paint
                        )

                        canvas.drawBitmap(
                            icon,
                            itemView.right.toFloat() - icon.width,
                            itemView.top.toFloat() + (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height.toFloat()) / 2,
                            paint
                        )
                    }
                    viewHolder.itemView.translationX = dX


                } else {
                    super.onChildDraw(
                        canvas,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }


        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)
        itemTouchHelper.attachToRecyclerView(binding.todoRv)
    }

    fun handleNotificationsForChat(messages: String, context: Context) {
        val channelId = "11"

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_access_time_black_24dp)


        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.putExtra("head", messages)

        val pendingIntent = PendingIntent.getActivity(
            context,
            111,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        notification.setContentIntent(pendingIntent)

        with(NotificationManagerCompat.from(context)) {
            notify(11, notification.build())
        }

    }
}