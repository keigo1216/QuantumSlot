package com.example.quantumslot

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment

class SelectCircuit: DialogFragment() {

    public interface DialogListner{
//        public fun onDialogCircuitRecueve(dialog: DialogFragment, addCircuit: Int)
//        public fun onDialogPositive(dialog: DialogFragment)//今回は使わない。色んなダイアログで使いまわす際には使います。
//        public fun onDialogNegative(dialog: DialogFragment)
        public fun onDialogCircuit(
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
            angles6: Int)//Activity側へStringを渡します。
    }
    var lister:DialogListner? = null

    var addCircuit1:Int? = null
    var addCircuit2:Int? = null
    var addCircuit3:Int? = null
    var addCircuit4:Int? = null
    var addCircuit5:Int? = null
    var addCircuit6:Int? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        //activityは呼び出しもと
        val builder = AlertDialog.Builder(activity)

        val inflater = requireActivity().layoutInflater
        //inflate: 拡張する
        val signinView = inflater.inflate(R.layout.fragment_selectcircuit, null)

        //リスナを取得
        val needChipText = signinView.findViewById<TextView>(R.id.need_chip)
        val noneGate = signinView.findViewById<ImageButton>(R.id.none_gate)
        val xGateButton = signinView.findViewById<ImageButton>(R.id.x_gate)
        val ryGateButton = signinView.findViewById<ImageButton>(R.id.ry_gate)
        val hGateButton = signinView.findViewById<ImageButton>(R.id.h_gate)
        val setCircuit = signinView.findViewById<LinearLayout>(R.id.setCircuit)
        val setAngles = signinView.findViewById<LinearLayout>(R.id.setAngles)

        //クリックされているボタンを格納する変数
//        var add_circuit = requireArguments().getString("circuit")!!.toInt()
        addCircuit1 = requireArguments().getString("circuit1")!!.toInt()
        addCircuit2 = requireArguments().getString("circuit2")!!.toInt()
        addCircuit3 = requireArguments().getString("circuit3")!!.toInt()
        addCircuit4 = requireArguments().getString("circuit4")!!.toInt()
        addCircuit5 = requireArguments().getString("circuit5")!!.toInt()
        addCircuit6 = requireArguments().getString("circuit6")!!.toInt()
        var angles1 = requireArguments().getString("angles1")!!.toInt()
        var angles2 = requireArguments().getString("angles2")!!.toInt()
        var angles3 = requireArguments().getString("angles3")!!.toInt()
        var angles4 = requireArguments().getString("angles4")!!.toInt()
        var angles5 = requireArguments().getString("angles5")!!.toInt()
        var angles6 = requireArguments().getString("angles6")!!.toInt()

        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val qc11 = signinView.findViewById<Switch>(R.id.circuit1)
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val qc12 = signinView.findViewById<Switch>(R.id.circuit2)
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val qc21 = signinView.findViewById<Switch>(R.id.circuit3)
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val qc22 = signinView.findViewById<Switch>(R.id.circuit4)
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val qc31 = signinView.findViewById<Switch>(R.id.circuit5)
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val qc32 = signinView.findViewById<Switch>(R.id.circuit6)

        var choseCircuitNow = 1 //現在選択しているボタン
        var needChip = 0 //現在ゲートを選択するときに必要になるボタン

        val angles1Bar = signinView.findViewById<SeekBar>(R.id.angles1)
        val angles2Bar = signinView.findViewById<SeekBar>(R.id.angles2)
        val angles3Bar = signinView.findViewById<SeekBar>(R.id.angles3)
        val angles4Bar = signinView.findViewById<SeekBar>(R.id.angles4)
        val angles5Bar = signinView.findViewById<SeekBar>(R.id.angles5)
        val angles6Bar = signinView.findViewById<SeekBar>(R.id.angles6)

        Log.w("DEBUG", angles1.toString())
        setAngles.visibility = View.INVISIBLE

        angles1Bar.progress = angles1
        angles2Bar.progress = angles2
        angles3Bar.progress = angles3
        angles4Bar.progress = angles4
        angles5Bar.progress = angles5
        angles6Bar.progress = angles6

        //ゲートを適応させないゲート
        noneGate.setOnClickListener {
            noneGate.setBackgroundResource(R.drawable.none_gate_pushed)
            xGateButton.setBackgroundResource(R.drawable.x_gate)
            hGateButton.setBackgroundResource(R.drawable.h_gate)
            ryGateButton.setBackgroundResource(R.drawable.ry_gate)
            setAngles.visibility = View.INVISIBLE
//            invisibleSeekBar(signinView)
//            add_circuit = 1 //Noneの時は1
            updateCircuitGate(signinView, choseCircuitNow) //以前選択していたボタンを保存する
            choseCircuitNow = 1 //現在選択しているボタンを更新
            initswich(signinView, choseCircuitNow) //現在のボタンの過去のデータを反映させる
        }

        //Xゲートを適応さセル回路を選ぶ
        xGateButton.setOnClickListener {
            noneGate.setBackgroundResource(R.drawable.none_gate)
            hGateButton.setBackgroundResource(R.drawable.h_gate)
            ryGateButton.setBackgroundResource(R.drawable.ry_gate) //別のボタンは元に戻す
            xGateButton.setBackgroundResource(R.drawable.x_gate_pushed) //押したボタン
            setAngles.visibility = View.INVISIBLE
            updateCircuitGate(signinView, choseCircuitNow) //以前選択していたボタンを保存する
            choseCircuitNow = 2 //現在選択しているボタンを更新
            initswich(signinView, choseCircuitNow) //現在のボタンの過去のデータを反映させる
        }

        //Hゲートを適応させる回路を選ぶ
        hGateButton.setOnClickListener {
            noneGate.setBackgroundResource(R.drawable.none_gate)
            xGateButton.setBackgroundResource(R.drawable.x_gate)
            ryGateButton.setBackgroundResource(R.drawable.ry_gate)
            hGateButton.setBackgroundResource(R.drawable.h_gate_pushed)
            updateCircuitGate(signinView, choseCircuitNow) //以前選択していたボタンを保存する
            choseCircuitNow = 4 //現在選択しているボタンを更新
            initswich(signinView, choseCircuitNow) //現在のボタンの過去のデータを反映させる
            setAngles.visibility = View.INVISIBLE
        }

        //RYゲートを適応させる回路を選ぶ
        ryGateButton.setOnClickListener {
            noneGate.setBackgroundResource(R.drawable.none_gate)
            xGateButton.setBackgroundResource(R.drawable.x_gate) //罰のボタンは元に戻す
            hGateButton.setBackgroundResource(R.drawable.h_gate)
            ryGateButton.setBackgroundResource(R.drawable.ry_gate_pushed) //押したボタン
//            add_circuit = 3 //RYゲートのデフォルトは3
            updateCircuitGate(signinView, choseCircuitNow)
            choseCircuitNow = 3 //現在選択しているボタンを更新
            initswich(signinView, choseCircuitNow)
            setAngles.visibility = View.VISIBLE
        }

        qc11.setOnCheckedChangeListener{ buttonView, isChecked ->
            angles1Bar.isEnabled = isChecked
        }

        qc12.setOnCheckedChangeListener { buttonView, isChecked ->
            angles2Bar.isEnabled = isChecked
        }
        qc21.setOnCheckedChangeListener { buttonView, isChecked ->
            angles3Bar.isEnabled = isChecked
        }
        qc22.setOnCheckedChangeListener { buttonView, isChecked ->
            angles4Bar.isEnabled = isChecked
        }
        qc31.setOnCheckedChangeListener { buttonView, isChecked ->
            angles5Bar.isEnabled = isChecked
        }
        qc32.setOnCheckedChangeListener { buttonView, isChecked ->
            angles6Bar.isEnabled = isChecked
        }

        builder.setView(signinView)
            .setPositiveButton("OK") { dialog, id ->
//                Log.w("DEBUG", add_circuit.toString())
                angles1 = angles1Bar.progress
                angles2 = angles2Bar.progress
                angles3 = angles3Bar.progress
                angles4 = angles4Bar.progress
                angles5 = angles5Bar.progress
                angles6 = angles6Bar.progress
                Log.w("DEBUG", angles1.toString())
                lister?.onDialogCircuit(this, this.addCircuit1!!, this.addCircuit2!!, this.addCircuit3!!, this.addCircuit4!!, this.addCircuit5!!, this.addCircuit6!!, angles1, angles2, angles3, angles4, angles5, angles6)
            }

        return builder.create()
    }

    //listnerの初期化
    //呼び出しもとはDialogListnerを継承しているから型変換可能
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            lister = context as DialogListner
        }catch (e: Exception){
            Log.e("ERROR", "CANNOT FIND LISTNER")
        }
    }

    override fun onDetach() {
        super.onDetach()
        lister = null
    }

    private fun initswich(signinView: View, choseCircuit: Int){
        Log.w("DEBUG", choseCircuit.toString())
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val qc11 = signinView.findViewById<Switch>(R.id.circuit1)
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val qc12 = signinView.findViewById<Switch>(R.id.circuit2)
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val qc21 = signinView.findViewById<Switch>(R.id.circuit3)
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val qc22 = signinView.findViewById<Switch>(R.id.circuit4)
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val qc31 = signinView.findViewById<Switch>(R.id.circuit5)
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val qc32 = signinView.findViewById<Switch>(R.id.circuit6)

        val angles1Bar = signinView.findViewById<SeekBar>(R.id.angles1)
        val angles2Bar = signinView.findViewById<SeekBar>(R.id.angles2)
        val angles3Bar = signinView.findViewById<SeekBar>(R.id.angles3)
        val angles4Bar = signinView.findViewById<SeekBar>(R.id.angles4)
        val angles5Bar = signinView.findViewById<SeekBar>(R.id.angles5)
        val angles6Bar = signinView.findViewById<SeekBar>(R.id.angles6)

        qc11.isChecked = (this.addCircuit1 == choseCircuit)
        qc12.isChecked = (this.addCircuit2 == choseCircuit)
        qc21.isChecked = (this.addCircuit3 == choseCircuit)
        qc22.isChecked = (this.addCircuit4 == choseCircuit)
        qc31.isChecked = (this.addCircuit5 == choseCircuit)
        qc32.isChecked = (this.addCircuit6 == choseCircuit)

        //チェックが入っていないSeekBarをEnableにする
        angles1Bar.isEnabled = (this.addCircuit1 == choseCircuit)
        angles2Bar.isEnabled = (this.addCircuit2 == choseCircuit)
        angles3Bar.isEnabled = (this.addCircuit3 == choseCircuit)
        angles4Bar.isEnabled = (this.addCircuit4 == choseCircuit)
        angles5Bar.isEnabled = (this.addCircuit5 == choseCircuit)
        angles6Bar.isEnabled = (this.addCircuit6 == choseCircuit)
    }

    private fun updateCircuitGate(signinView: View, circuit:Int) {
//        val needChipPerGate:Int
//        //一つの回路を作るのに必要なチップの数
//        when(circuit){
//            1 -> needChipPerGate = 0 //Noneを選んだ時
//            2 -> needChipPerGate = 1 //Xを選んだ時
//            3 -> needChipPerGate = 2 //Hを選んだ時
//            4 -> needChipPerGate = 4 //RYを選んだ時
//        }
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val qc11 = signinView.findViewById<Switch>(R.id.circuit1)
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val qc12 = signinView.findViewById<Switch>(R.id.circuit2)
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val qc21 = signinView.findViewById<Switch>(R.id.circuit3)
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val qc22 = signinView.findViewById<Switch>(R.id.circuit4)
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val qc31 = signinView.findViewById<Switch>(R.id.circuit5)
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        val qc32 = signinView.findViewById<Switch>(R.id.circuit6)
        if(qc11.isChecked) this.addCircuit1 = circuit
        if(qc12.isChecked) this.addCircuit2 = circuit
        if(qc21.isChecked) this.addCircuit3 = circuit
        if(qc22.isChecked) this.addCircuit4 = circuit
        if(qc31.isChecked) this.addCircuit5 = circuit
        if(qc32.isChecked) this.addCircuit6 = circuit
    }
}