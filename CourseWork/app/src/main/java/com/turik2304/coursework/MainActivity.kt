package com.turik2304.coursework

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.turik2304.coursework.databinding.ActivityMainBinding
import com.turik2304.coursework.recyclerViewBase.ViewTyped
import com.turik2304.coursework.databinding.ChatListBinding
import com.turik2304.coursework.recyclerViewBase.Adapter
import com.turik2304.coursework.recyclerViewBase.HolderFactory
import com.turik2304.coursework.recyclerViewBase.holders.MessageUI
import com.turik2304.coursework.recyclerViewBase.holders.TextUI

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ChatListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ChatListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val clickListener = { view: View ->
            Toast.makeText(this, "OPOPO", Toast.LENGTH_SHORT).show()
        }
        val holderFactory = ChatHolderFactory(clickListener)
        val adapter = Adapter<ViewTyped>(holderFactory)
        binding.recycleView.adapter = adapter

        val messageTest1 = MessageUI("test1")
        val messageTest2 = MessageUI("test2")
        val messageTest3 = MessageUI("test3")
        val messageTest4 = MessageUI("test4")
        val messageTest5 = MessageUI("test5")
        val messageTest6 = MessageUI("test6")
        val messageTest7 = MessageUI("test7")
        val messageTest8 = MessageUI("test8")
        val messageTest9 = MessageUI("test9")
        val messageTest10 = MessageUI("test10")




//        val message1 = MessageUI("Artur Sibagatullin",
//            "Тестовое сообщение, которое я сделал!")
//
//        val message2 = MessageUI("Denis Mashkov",
//            "Тестовое сообщение, которое я сделал! Тестовое сообщение, которое я сделал!")
//
//        val message3 = MessageUI("Evgeny Sobko",
//            "Тестовое сообщение, которое сделал Evgeny! ")
//
//        val message4 = MessageUI("Alexander Pakulev",
//            "Тестовое сообщение, которое сделал Alexander!")
//
//        val message5 = MessageUI("Stepan Kanov",
//            "Тестовое сообщение, которое сделал Stepan!")
//
//        val message6 = MessageUI("Alexey Kolesnikov",
//            "Тестовое сообщение, которое сделал Alexey!")
//
//        val message7 = MessageUI("Sibagatullin Artur",
//            "Тестовое сообщение, которое сделал Artur!")

        adapter.items = listOf(
            messageTest1,
            messageTest2,
            messageTest3,
            messageTest4,
            messageTest5,
            messageTest6,
            messageTest7,
            messageTest8,
            messageTest9,
            messageTest10,
            TextUI("TEST TEXT", R.layout.item_text)
        )

    }

}