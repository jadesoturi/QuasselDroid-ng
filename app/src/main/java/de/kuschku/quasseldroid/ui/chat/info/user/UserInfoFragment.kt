package de.kuschku.quasseldroid.ui.chat.info.user

import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.ButterKnife
import de.kuschku.libquassel.protocol.Buffer_Type
import de.kuschku.libquassel.quassel.syncables.IrcUser
import de.kuschku.libquassel.util.IrcUserUtils
import de.kuschku.quasseldroid.GlideApp
import de.kuschku.quasseldroid.R
import de.kuschku.quasseldroid.ui.chat.ChatActivity
import de.kuschku.quasseldroid.ui.chat.input.AutoCompleteHelper.Companion.IGNORED_CHARS
import de.kuschku.quasseldroid.util.AvatarHelper
import de.kuschku.quasseldroid.util.helper.*
import de.kuschku.quasseldroid.util.irc.format.ContentFormatter
import de.kuschku.quasseldroid.util.service.ServiceBoundFragment
import de.kuschku.quasseldroid.util.ui.LinkLongClickMenuHelper
import de.kuschku.quasseldroid.util.ui.TextDrawable
import io.reactivex.Observable
import me.saket.bettermovementmethod.BetterLinkMovementMethod
import javax.inject.Inject

class UserInfoFragment : ServiceBoundFragment() {
  @BindView(R.id.avatar)
  lateinit var avatar: ImageView

  @BindView(R.id.nick)
  lateinit var nick: TextView

  @BindView(R.id.real_name)
  lateinit var realName: TextView

  @BindView(R.id.action_query)
  lateinit var actionQuery: Button

  @BindView(R.id.action_ignore)
  lateinit var actionIgnore: Button

  @BindView(R.id.action_whois)
  lateinit var actionWhois: Button

  @BindView(R.id.action_mention)
  lateinit var actionMention: Button

  @BindView(R.id.away_container)
  lateinit var awayContainer: ViewGroup

  @BindView(R.id.away_message)
  lateinit var awayMessage: TextView

  @BindView(R.id.account_container)
  lateinit var accountContainer: ViewGroup

  @BindView(R.id.account)
  lateinit var account: TextView

  @BindView(R.id.ident_container)
  lateinit var identContainer: ViewGroup

  @BindView(R.id.ident)
  lateinit var ident: TextView

  @BindView(R.id.host_container)
  lateinit var hostContainer: ViewGroup

  @BindView(R.id.host)
  lateinit var host: TextView

  @BindView(R.id.server_container)
  lateinit var serverContainer: ViewGroup

  @BindView(R.id.server)
  lateinit var server: TextView

  @Inject
  lateinit var contentFormatter: ContentFormatter

  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View? {
    val view = inflater.inflate(R.layout.fragment_info_user, container, false)
    ButterKnife.bind(this, view)

    val openBuffer = arguments?.getBoolean("openBuffer")

    val senderColors = requireContext().theme.styledAttributes(
      R.attr.senderColor0, R.attr.senderColor1, R.attr.senderColor2, R.attr.senderColor3,
      R.attr.senderColor4, R.attr.senderColor5, R.attr.senderColor6, R.attr.senderColor7,
      R.attr.senderColor8, R.attr.senderColor9, R.attr.senderColorA, R.attr.senderColorB,
      R.attr.senderColorC, R.attr.senderColorD, R.attr.senderColorE, R.attr.senderColorF
    ) {
      IntArray(length()) {
        getColor(it, 0)
      }
    }

    val networkId = arguments?.getInt("networkId")
    val nickName = arguments?.getString("nick")
    combineLatest(viewModel.session, viewModel.networks).switchMap { (sessionOptional, networks) ->
      if (openBuffer == true) {
        val session = sessionOptional?.orNull()
        val bufferSyncer = session?.bufferSyncer
        val bufferInfo = bufferSyncer?.bufferInfo(arguments?.getInt("bufferId") ?: -1)
        bufferInfo?.let {
          networks[it.networkId]?.liveIrcUser(it.bufferName)
        }
      } else {
        networks[networkId]?.liveIrcUser(nickName)
      } ?: Observable.just(IrcUser.NULL)
    }.filter {
      it != IrcUser.NULL
    }.switchMap(IrcUser::updates).toLiveData().observe(this, Observer { user ->
      if (user != null) {
        val senderColorIndex = IrcUserUtils.senderColor(user.nick())
        val rawInitial = user.nick().trimStart(*IGNORED_CHARS).firstOrNull()
                         ?: user.nick().firstOrNull()
        val initial = rawInitial?.toUpperCase().toString()
        val senderColor = senderColors[senderColorIndex]

        val fallbackDrawable = TextDrawable.builder().buildRect(initial, senderColor)

        val avatarUrl = AvatarHelper.avatar(user = user)
        if (avatarUrl != null) {
          GlideApp.with(avatar)
            .load(avatarUrl)
            .placeholder(fallbackDrawable)
            .into(avatar)
        } else {
          avatar.setImageDrawable(fallbackDrawable)
        }

        nick.text = user.nick()
        realName.text = contentFormatter.format(requireContext(), user.realName())
        realName.visibleIf(user.realName().isNotBlank() && user.realName() != user.nick())

        awayMessage.text = user.awayMessage()
        awayContainer.visibleIf(user.awayMessage().isNotBlank())

        account.text = user.account()
        accountContainer.visibleIf(user.account().isNotBlank())

        ident.text = user.user()
        identContainer.visibleIf(user.user().isNotBlank())

        host.text = user.host()
        hostContainer.visibleIf(user.host().isNotBlank())

        server.text = user.server()
        serverContainer.visibleIf(user.server().isNotBlank())

        actionQuery.setOnClickListener {
          viewModel.session {
            it.orNull()?.let { session ->
              val info = session.bufferSyncer?.find(
                bufferName = user.nick(),
                networkId = user.network().networkId(),
                type = Buffer_Type.of(Buffer_Type.QueryBuffer)
              )

              val intent = Intent(requireContext(), ChatActivity::class.java)
              if (info != null) {
                intent.putExtra("bufferId", info.bufferId)
                startActivity(intent)
              } else {
                viewModel.allBuffers.map {
                  listOfNotNull(it.find {
                    it.networkId == user.network().networkId() && it.bufferName == user.nick()
                  })
                }.filter {
                  it.isNotEmpty()
                }.firstElement().toLiveData().observe(this, Observer {
                  it?.firstOrNull()?.let { info ->
                    intent.putExtra("bufferId", info.bufferId)
                    startActivity(intent)
                  }
                })

                session.bufferSyncer?.find(
                  networkId = networkId,
                  type = Buffer_Type.of(Buffer_Type.StatusBuffer)
                )?.let { statusInfo ->
                  session.rpcHandler?.sendInput(statusInfo, "/query ${user.nick()}")
                }
              }
            }
          }
        }

        actionIgnore.setOnClickListener {
          Toast.makeText(requireContext(), "Not Implemented", Toast.LENGTH_SHORT).show()
        }

        actionWhois.setOnClickListener {
          viewModel.session {
            it.orNull()?.let { session ->
              session.bufferSyncer?.find(
                networkId = networkId,
                type = Buffer_Type.of(Buffer_Type.StatusBuffer)
              )?.let { statusInfo ->
                session.rpcHandler?.sendInput(statusInfo, "/whois ${user.nick()}")
              }
            }
          }
        }

        actionMention.setOnClickListener {
          val intent = Intent(requireContext(), ChatActivity::class.java)
          intent.type = "text/plain"
          intent.putExtra(Intent.EXTRA_TEXT, "${user.nick()}: ")
          startActivity(intent)
        }
        actionMention.visibleIf(arguments?.getBoolean("openBuffer") == false)
      }
    })

    val movementMethod = BetterLinkMovementMethod.newInstance()
    movementMethod.setOnLinkLongClickListener(LinkLongClickMenuHelper())
    realName.movementMethod = movementMethod

    actionQuery.setTooltip()
    actionQuery.retint()
    actionIgnore.setTooltip()
    actionIgnore.retint()
    actionWhois.setTooltip()
    actionWhois.retint()
    actionMention.setTooltip()
    actionMention.retint()

    return view
  }
}