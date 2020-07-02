package de.thm.mow.boardgame

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding
import de.thm.mow.boardgame.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity(), View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private var game: Game = GenericGame(CheckersGameLogic())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val contentView = View.inflate(this, R.layout.activity_main, null)
        val boardView = contentView.findViewById<LinearLayout>(R.id.board)
        for (y in 0 until 8) {
            val row = LinearLayout(this)
            row.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0, 1.0F)
            for (x in 0 until 8) {
                val cell = Button(this)
                cell.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1.0F)
                row.addView(cell)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    cell.setAutoSizeTextTypeWithDefaults(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM)
                }
                cell.setPadding(-45); cell.includeFontPadding = false
                cell.tag = Coords(x, y)
                cell.setOnClickListener(this)
            }
            boardView.addView(row)
        }
        setContentView(contentView)

        findViewById<SeekBar>(R.id.seekBar).setOnSeekBarChangeListener(this)
        restart(null)
    }

    override fun onClick(v: View?) {
        if (v == null || !game.isCurrentPlayerWhite) return
        game.userAction(v.tag as Coords)
        refreshUI()
        if (!game.isCurrentPlayerWhite) {
            GlobalScope.launch(Dispatchers.Main) {
                withContext(Dispatchers.Default) {game.aiMove()}
                refreshUI()
            }
        }
    }

    fun restart(v: View?) {
        game.restart()
        refreshUI()
    }

    fun selectGame(v: View?) {
        game = if (v?.id == R.id.rbCheckers) GenericGame(CheckersGameLogic()) else GenericGame(ReversiGameLogic())
        refreshUI()
    }

    private fun refreshUI() {
        for (x in 0 until 8) {
            for (y in 0 until 8) {
                val cell = findViewById<LinearLayout>(R.id.board).findViewWithTag<Button>(Coords(x, y))
                cell.text = game.getFieldAsString(Coords(x, y))
                var backgroundColor = if ((x + y) % 2 == 0) Color.WHITE else Color.GRAY
                if (game.getCurrentTargets().contains(Coords(x, y))) backgroundColor = Color.GREEN
                cell.setBackgroundColor(backgroundColor)
            }
        }
        var status = ""
        if (game.result.finished) {
            when (game.result.winner) {
                Player.white -> status = "White wins."
                Player.black -> status = "Black wins."
                null -> status = "draw"
            }
        } else {
            status = if (game.isCurrentPlayerWhite) "White's turn" else "Black's turn"
        }
        findViewById<TextView>(R.id.tvStatus).text = status
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        findViewById<TextView>(R.id.tvAiDepth).text = "AI search depth: ${progress+1}"
        game.aiSetSearchDepth(progress+1)
    }
    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
}