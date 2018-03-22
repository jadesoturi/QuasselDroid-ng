package de.kuschku.quasseldroid_ng.ui.chat.nicks

import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.quasseldroid_ng.R
import de.kuschku.quasseldroid_ng.util.helper.visibleIf

class NickListAdapter(
  private val clickListener: ((String) -> Unit)? = null
) : ListAdapter<NickListAdapter.IrcUserItem, NickListAdapter.NickViewHolder>(
  object : DiffUtil.ItemCallback<IrcUserItem>() {
    override fun areItemsTheSame(oldItem: IrcUserItem, newItem: IrcUserItem) =
      oldItem.nick == newItem.nick

    override fun areContentsTheSame(oldItem: IrcUserItem?, newItem: IrcUserItem?) =
      oldItem == newItem
  }) {
  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    NickViewHolder(
      LayoutInflater.from(parent.context).inflate(
        when (viewType) {
          VIEWTYPE_AWAY -> R.layout.widget_nick_away
          else          -> R.layout.widget_nick
        }, parent, false
      ),
      clickListener = clickListener
    )

  override fun onBindViewHolder(holder: NickViewHolder, position: Int) =
    holder.bind(getItem(position))

  override fun getItemViewType(position: Int) = if (getItem(position).away) {
    VIEWTYPE_AWAY
  } else {
    VIEWTYPE_ACTIVE
  }

  data class IrcUserItem(
    val nick: String,
    val modes: String,
    val lowestMode: Int,
    val realname: CharSequence,
    val away: Boolean,
    val networkCasemapping: String
  )

  class NickViewHolder(
    itemView: View,
    private val clickListener: ((String) -> Unit)? = null
  ) : RecyclerView.ViewHolder(itemView) {
    @BindView(R.id.modesContainer)
    lateinit var modesContainer: View

    @BindView(R.id.modes)
    lateinit var modes: TextView

    @BindView(R.id.nick)
    lateinit var nick: TextView

    @BindView(R.id.realname)
    lateinit var realname: TextView

    var user: String? = null

    init {
      ButterKnife.bind(this, itemView)
      itemView.setOnClickListener {
        val nick = user
        if (nick != null)
          clickListener?.invoke(nick)
      }
    }

    fun bind(data: IrcUserItem) {
      user = data.nick

      nick.text = data.nick
      modes.text = data.modes
      realname.text = data.realname

      modes.visibleIf(data.modes.isNotBlank())
    }
  }

  companion object {
    val VIEWTYPE_ACTIVE = 0
    val VIEWTYPE_AWAY = 1
  }
}