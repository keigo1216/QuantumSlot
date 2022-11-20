package com.example.quantumslot

import android.content.Context
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
//import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.Socket
import java.net.SocketTimeoutException
import java.net.URL
import java.util.concurrent.ThreadLocalRandom
import java.util.stream.BaseStream
//import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
//import com.fasterxml.jackson.module.kotlin.readValue

////JSONへ変換する時に使うクラス
//data class RequestAngle(val chip: Int, val add_circuit: Int, val angles1: Float, val angles2: Float, val angles3: Float, val angles4: Float, val angles5: Float, val angles6: Float)
//data class RequestNoAngle(val chip: Int, val add_circuit: Int)

class MainActivity : AppCompatActivity(), SelectCircuit.DialogListner {

    //定数の宣言
    companion object {
        private const val SERVER_URL = "http://10.0.2.2:8080"
    }

    var chip: Int? = null
    var addCircuit1:Int? = null
    var addCircuit2:Int? = null
    var addCircuit3:Int? = null
    var addCircuit4:Int? = null
    var addCircuit5:Int? = null
    var addCircuit6:Int? = null
    var angles1: Int? = null
    var angles2: Int? = null
    var angles3: Int? = null
    var angles4: Int? = null
    var angles5: Int? = null
    var angles6: Int? = null

    val handler = Handler(Looper.getMainLooper())
    val slotShow = object : Runnable{
        override fun run() {
            val slot1 = findViewById<ImageView>(R.id.slot1)
            val slot2 = findViewById<ImageView>(R.id.slot2)
            val slot3 = findViewById<ImageView>(R.id.slot3)
            val slot1ImageRandom = ThreadLocalRandom.current().nextInt(0, 5)
            val slot2ImageRandom = ThreadLocalRandom.current().nextInt(0, 5)
            val slot3ImageRandom = ThreadLocalRandom.current().nextInt(0, 5)
            val imageViewId1 = resources.getIdentifier(getImageName(slot1ImageRandom), "drawable", packageName)
            val imageViewId2 = resources.getIdentifier(getImageName(slot2ImageRandom), "drawable", packageName)
            val imageViewId3 = resources.getIdentifier(getImageName(slot3ImageRandom), "drawable", packageName)
            slot1.setImageResource(imageViewId1)
            slot2.setImageResource(imageViewId2)
            slot3.setImageResource(imageViewId3)
            handler.postDelayed(this, 100)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //スタートボタンを押した時の処理
        val startButton = findViewById<Button>(R.id.slotStart)
        startButton.setOnClickListener{
            if(this.chip!! > 0){
                startButton.isEnabled = false //ボタンを押せなくなる
                receiveSlotInfo(SERVER_URL)
            }
        }

        val selectButton = findViewById<Button>(R.id.selectCircuit)
        selectButton.setOnClickListener {
            //フラグメントに送る引数を定義
            //Bundleはandroidシステムに一度渡す
            //アプリが落ちても大丈夫なようになっている
            val args = Bundle()
            args.putString("circuit1", this.addCircuit1.toString())
            args.putString("circuit2", this.addCircuit2.toString())
            args.putString("circuit3", this.addCircuit3.toString())
            args.putString("circuit4", this.addCircuit4.toString())
            args.putString("circuit5", this.addCircuit5.toString())
            args.putString("circuit6", this.addCircuit6.toString())
            args.putString("angles1", this.angles1.toString())
            args.putString("angles2", this.angles2.toString())
            args.putString("angles3", this.angles3.toString())
            args.putString("angles4", this.angles4.toString())
            args.putString("angles5", this.angles5.toString())
            args.putString("angles6", this.angles6.toString())
            val dialog = SelectCircuit()
            dialog.arguments = args
            dialog.show(supportFragmentManager, "simple")
        }

        this.chip = 20
        this.addCircuit1 = 1
        this.addCircuit2 = 1
        this.addCircuit3 = 1
        this.addCircuit4 = 1
        this.addCircuit5 = 1
        this.addCircuit6 = 1
        this.angles1 = 0
        this.angles2 = 0
        this.angles3 = 0
        this.angles4 = 0
        this.angles5 = 0
        this.angles6 = 0
    }

    //ポップアップするFragmentとお話しする
    //onDialogFragmentを継承するために関数を定義する
    override fun onDialogCircuit(
        dialog: DialogFragment,
        addCircuit1: Int,
        addCircuit2: Int,
        addCircuit3: Int,
        addCircuit4: Int,
        addCircuit5: Int,
        addCircuit6: Int,
        angles1: Int,
        angles2: Int,
        angles3: Int,
        angles4: Int,
        angles5: Int,
        angles6: Int
    ) {
        this.addCircuit1 = addCircuit1
        this.addCircuit2 = addCircuit2
        this.addCircuit3 = addCircuit3
        this.addCircuit4 = addCircuit4
        this.addCircuit5 = addCircuit5
        this.addCircuit6 = addCircuit6
        this.angles1 = angles1
        this.angles2 = angles2
        this.angles3 = angles3
        this.angles4 = angles4
        this.angles5 = angles5
        this.angles6 = angles6

        var chipUse = 0
        chipUse += needChipPerCircuit(this.addCircuit1!!)
        chipUse += needChipPerCircuit(this.addCircuit2!!)
        chipUse += needChipPerCircuit(this.addCircuit3!!)
        chipUse += needChipPerCircuit(this.addCircuit4!!)
        chipUse += needChipPerCircuit(this.addCircuit5!!)
        chipUse += needChipPerCircuit(this.addCircuit6!!)

        val chipText = findViewById<TextView>(R.id.chip_use_circuit)
        chipText.text = chipUse.toString()

        Log.w("DEBUG", "angles1 : ${this.angles1}")
        Log.w("DEBUG", "angles2 : ${this.angles2}")
        Log.w("DEBUG", "angles3 : ${this.angles3}")
        Log.w("DEBUG", "angles4 : ${this.angles4}")
        Log.w("DEBUG", "angles5 : ${this.angles5}")
        Log.w("DEBUG", "angles6 : ${this.angles6}")
    }

    private fun needChipPerCircuit(addCircuit: Int): Int{
        return when(addCircuit){
            1 -> 0
            2 -> 1
            3 -> 3
            else -> 2
        }
    }

    //サーバーとの非同期処理
    //コルーチンで処理を記述
    //コルーチン: ある処理を中断、再開できるインスタンスのこと（軽量なスレッド）
    //スレッドと似ているが、コルーチン間でのデータの受け渡しが簡単にできる
    @UiThread
    private fun receiveSlotInfo(urlFll: String){
        //コルーチンを生成
        //この中に書くものは実行スレッドとは別で、並列に実行される。ブロックしない
        lifecycleScope.launch {
            val result = slotInfoBackgroundRunner(urlFll)
            slotInfoPostRunner(result)
            val startButton = findViewById<Button>(R.id.slotStart)
            startButton.isEnabled = true
        }
        handler.post(slotShow)
    }

    //suspend: この関数を実行しているときは同一のコルーチン内の他の処理を停止させる
    @WorkerThread
    private suspend fun slotInfoBackgroundRunner(url: String): String {
        val returnVal = withContext(Dispatchers.IO){
            var result = ""
            val request = "chip=${chip}&add_circuit1=${addCircuit1}&add_circuit2=${addCircuit2}&add_circuit3=${addCircuit3}&add_circuit4=${addCircuit4}&add_circuit5=${addCircuit5}&add_circuit6=${addCircuit6}&angles1=${angles1}&angles2=${angles2}&angles3=${angles3}&angles4=${angles4}&angles5=${angles5}&angles6=${angles6}"
            val url = URL(url) //urlオブジェクトの生成
            val con = url.openConnection() as? HttpURLConnection //HTTPURLConnectionの生成
            con?.let {
                try{
                    it.connectTimeout = 50000
                    it.readTimeout = 50000
                    it.requestMethod = "POST"
                    it.doOutput = true //書き込み
                    it.connect() //接続
                    val outStream = it.outputStream //レクエストのbodyを書くところ
                    outStream.write(request.toByteArray())
                    outStream.flush()
                    outStream.close() //解放
                    val inStream = it.inputStream //レスポンスデータの取得
                    result = is2String(inStream)
                    inStream.close() //InputStreamオブジェクトの解放
                }
                catch (ex: SocketTimeoutException){
                    Log.w("MainActivity", "通信タイムアウト", ex)
                }
                it.disconnect() //HttpURLConnectionの解放
            }
            result
        }
        return returnVal
    }

    private fun getImageName(slot: Int): String{
        return when (slot){
            1 -> "slot_mushroom"
            2 -> "slot_orange"
            3 -> "slot_melon"
            else -> "slot_grape"
        }
    }

    @UiThread
    private fun slotInfoPostRunner(result: String) {
        handler.removeCallbacks(slotShow) //ランダムで表示させている処理の停止
        val rootJSON = JSONObject(result)
        val resultJSON = rootJSON.getJSONObject("result")
        val slot1 = resultJSON.getInt("slot1")
        val slot2 = resultJSON.getInt("slot2")
        val slot3 = resultJSON.getInt("slot3")
        val inc = resultJSON.getInt("inc")
        val chip = resultJSON.getInt("chip")
        //結果の表示
        val imageView1 = findViewById<ImageView>(R.id.slot1)
        val imageView2 = findViewById<ImageView>(R.id.slot2)
        val imageView3 = findViewById<ImageView>(R.id.slot3)
        val imageViewId1 = resources.getIdentifier(getImageName(slot1), "drawable", packageName)
        val imageViewId2 = resources.getIdentifier(getImageName(slot2), "drawable", packageName)
        val imageViewId3 = resources.getIdentifier(getImageName(slot3), "drawable", packageName)
        imageView1.setImageResource(imageViewId1)
        imageView2.setImageResource(imageViewId2)
        imageView3.setImageResource(imageViewId3)

        this.chip = chip //ゲーム後のチップを設定

        val chipText = findViewById<TextView>(R.id.chip_text)
        chipText.text = this.chip.toString()

        //次のゲームの時のチップも表示させたほうがいいかも
    }

    private fun is2String(stream: InputStream): String{
        val sb = StringBuilder()
        val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
        var line = reader.readLine()
        while(line != null) {
            sb.append(line)
            line = reader.readLine()
        }
        reader.close()
        return sb.toString()
    }
}