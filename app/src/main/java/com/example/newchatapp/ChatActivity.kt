package com.example.newchatapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// 데이터 클래스 및 메시지 타입 정의
data class ChatMessage(
    val message: String,
    val type: MessageType
)

enum class MessageType {
    SENT,
    RECEIVED
}

// RecyclerView 어댑터
class ChatAdapter(private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_SENT = 1
        const val VIEW_TYPE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        return when (messages[position].type) {
            MessageType.SENT -> VIEW_TYPE_SENT
            MessageType.RECEIVED -> VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.sent_message_bubble, parent, false)
            SentMessageViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.received_message_bubble, parent, false)
            ReceivedMessageViewHolder(view)
        }
    }

    override fun getItemCount() = messages.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is SentMessageViewHolder -> holder.bind(message)
            is ReceivedMessageViewHolder -> holder.bind(message)
        }
    }

    // 내가 보낸 메시지를 위한 ViewHolder
    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val sentMessageBubble: TextView = itemView.findViewById(R.id.sent_message_bubble)
        fun bind(message: ChatMessage) {
            sentMessageBubble.text = message.message
        }
    }

    // 상대방이 보낸 메시지를 위한 ViewHolder
    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val receivedMessageBubble: TextView = itemView.findViewById(R.id.receive_message_bubble)
        fun bind(message: ChatMessage) {
            receivedMessageBubble.text = message.message
        }
    }
}

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageEditText: EditText
    private lateinit var sendButton: ImageButton
    private val chatMessages = mutableListOf<ChatMessage>()
    private lateinit var chatAdapter: ChatAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 뷰 초기화
        chatRecyclerView = findViewById(R.id.chat_recyclerView)
        messageEditText = findViewById(R.id.message_edit)
        sendButton = findViewById(R.id.send_btn)

        // RecyclerView 어댑터 및 레이아웃 매니저 설정
        chatAdapter = ChatAdapter(chatMessages)
        chatRecyclerView.adapter = chatAdapter
        chatRecyclerView.layoutManager = LinearLayoutManager(this)

        sendButton.setOnClickListener {
            // 1. 사용자가 입력한 메시지를 sentMessage 변수에 저장
            val sentMessage: String = messageEditText.text.toString().trim()
            if (sentMessage.isNotEmpty()) {
                // 2. 보낸 메시지 객체를 생성하여 RecyclerView에 추가 (sent_message_bubble.xml 사용)
                val newSentMessage = ChatMessage(sentMessage, MessageType.SENT)
                chatMessages.add(newSentMessage)
                chatAdapter.notifyItemInserted(chatMessages.size - 1)
                messageEditText.text.clear()

                // 3. 외부 코드로부터 받은 답변을 receivedMessage 변수에 저장
                // 예시로 더미 함수를 호출
                val replyMessage: String = externalReply(sentMessage)
                val newReceivedMessage = ChatMessage(replyMessage, MessageType.RECEIVED)
                chatMessages.add(newReceivedMessage)
                chatAdapter.notifyItemInserted(chatMessages.size - 1)
            }
        }
    }

    // 외부 코드와의 연동을 모방한 더미 함수 (실제 통신 로직으로 교체 가능)
    private fun externalReply(sentMsg: String): String {
        return "Reply to: $sentMsg"
    }
}