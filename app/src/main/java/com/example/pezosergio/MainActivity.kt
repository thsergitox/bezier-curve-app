package com.example.pezosergio

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.pezosergio.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.myToolbar)

        val mBitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
        val mCanvas = Canvas(mBitmap)

        mCanvas.drawColor(Color.WHITE)

        binding.myImg.setImageBitmap(mBitmap)

        val mPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 3F
            isAntiAlias = true
        }

        val height = mCanvas.height.toFloat()
        val width = mCanvas.width.toFloat()

        // Lets draw a grid
        val numLayout = 20f

        mCanvas.drawRect(0F, 0F, width, height, mPaint)
        mCanvas.drawText("0", numLayout, numLayout, mPaint)
        mCanvas.drawText("x", width - numLayout, numLayout, mPaint)
        mCanvas.drawText("y", numLayout, height - numLayout, mPaint)
        val originalBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true)

        Global.bezierNodes.forEach {node ->
            mCanvas.drawCircle(node.positionX, node.positionY, 5F, mPaint)
            mCanvas.drawText(node.idNode.toString(), node.positionX + numLayout, node.positionY + numLayout, mPaint)
        }


        fun bezierCurve() {
            if(Global.bezierOrderNodes.size >= 3){
                Global.bezierCurve.clear()
                val steps = 1000

                var t: Float
                for (i in 0..steps) {
                    t = i / steps.toFloat()

                    var tempPoints = Global.bezierOrderNodes.map { floatArrayOf(it.positionX, it.positionY) }.toMutableList()

                    while (tempPoints.size > 1) {
                        val newPoints = mutableListOf<FloatArray>()
                        for (j in 0 until tempPoints.size - 1) {
                            val x = (1 - t) * tempPoints[j][0] + t * tempPoints[j + 1][0]
                            val y = (1 - t) * tempPoints[j][1] + t * tempPoints[j + 1][1]
                            newPoints.add(floatArrayOf(x, y))
                        }
                        tempPoints = newPoints
                    }

                    mCanvas.drawCircle(tempPoints[0][0], tempPoints[0][1], 2F, mPaint)
                    Global.bezierCurve.add(floatArrayOf(tempPoints[0][0], tempPoints[0][1]))
                }


            }
        }
        var nodesBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true)
        bezierCurve()
        binding.myImg.setOnTouchListener(object: View.OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                if (event.action == MotionEvent.ACTION_DOWN) {
                    val touchX = event.x
                    val touchY = event.y

                    val scaleX = mBitmap.width.toFloat() / binding.myImg.width
                    val scaleY = mBitmap.height.toFloat() / binding.myImg.height

                    val x = touchX * scaleX
                    val y = touchY * scaleY
                    val numNodes = Global.numNodes++
                    val name = "Node ${numNodes}"

                    val node = Node(numNodes, name, x, y)
                    Global.bezierNodes.add(node)
                    Global.bezierOrderNodes.add(node)

                    if (Global.bezierNodes.size >= 3) mCanvas.drawBitmap(nodesBitmap!!, 0f, 0f, null)
                    mCanvas.drawCircle(x, y, 5F, mPaint)
                    mCanvas.drawText(Global.bezierNodes[Global.bezierNodes.size - 1].idNode.toString(), x + numLayout, y, mPaint)
                    nodesBitmap = mBitmap.copy(Bitmap.Config.ARGB_8888, true)

                    bezierCurve()
                    binding.seekBar.progress = 100

                    binding.myImg.invalidate()
                }
                return true
            }
        })

        binding.btnGenerateRoute.setOnClickListener(object : View.OnClickListener{
            @SuppressLint("SuspiciousIndentation")
            override fun onClick(v: View?) {
                var numCities = Global.numNodes
                var sizePopulation:Float = binding.txttampoblacion.getText().toString().toFloat()
                var mutationProbability:Float = binding.txtprobabilidadMutacion.getText().toString().toFloat()
                var numGenerations:Float = binding.txtnumgereraciones.getText().toString().toFloat()
                var coordinates = Global.bezierNodes.flatMap { listOf(it.positionX, it.positionY) }


                if ( numCities >= 4){
                    val requestData = RequestData(
                        listOf(numCities,sizePopulation,mutationProbability,numGenerations),
                        coordinates
                    )

                    val call = RetroFitTraveler.aTravelerAPI.predict(requestData)
                    call.enqueue(object : Callback<ResponseData> {
                        override fun onResponse(
                            call: Call<ResponseData>,
                            response: Response<ResponseData>
                        ) {
                            if(response.isSuccessful){
                                val orderNodes: ArrayList<Node> = ArrayList()
                                val responseData = response.body()
                                responseData?.let{
                                    var mensaje: String = ""
                                    for(i in 0..numCities){
                                        mensaje = mensaje +" - "+ it.prediction[i].toString()

                                        if(i < numCities) orderNodes.add(Global.bezierNodes[it.prediction[i]])
                                    }
                                    Global.bezierOrderNodes = orderNodes
                                    mensaje = "La mejor ruta es: $mensaje"
                                    binding.txtResult.text = mensaje
                                    mCanvas.drawBitmap(originalBitmap, 0f, 0f, null)
                                    Global.bezierNodes.forEach {node ->
                                        mCanvas.drawCircle(node.positionX, node.positionY, 5F, mPaint)
                                        mCanvas.drawText(node.idNode.toString(), node.positionX + numLayout, node.positionY + numLayout, mPaint)
                                    }
                                    bezierCurve()
                                    binding.myImg.invalidate()

                                }
                            }else{
                                val myToast = Toast.makeText(applicationContext,"Por favor, ingrese nuevos nodos, toque en Reset",Toast.LENGTH_LONG)
                                myToast.show()
                            }
                        }
                        override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                            var mensaje: String = t.message.toString()
                            val myToast = Toast.makeText(applicationContext,"Error2:"+mensaje,Toast.LENGTH_LONG)
                            myToast.show()
                        }
                    })
                }else{
                    Toast.makeText(applicationContext,"Debe ingresar al menos 4 ciudades",Toast.LENGTH_LONG).show()
                }





            }
        })


        binding.btnListNodes.setOnClickListener {
            intent = Intent(applicationContext, MainList::class.java)
            startActivity(intent)
        }

        binding.btnReset.setOnClickListener {
            Global.bezierNodes.clear()
            Global.bezierOrderNodes.clear()
            Global.bezierCurve.clear()
            Global.numNodes = 0
            binding.txtResult.text = ""
            mCanvas.drawBitmap(originalBitmap, 0f, 0f, null)
        }

        binding.seekBar.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
            @SuppressLint("SetTextI18n")
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mCanvas.drawBitmap(nodesBitmap!!, 0f, 0f, null)
                val scale = Global.bezierCurve.size.toFloat() * progress.toFloat() / 100f

                binding.txtT.text = "t = ${scale/1000f }"

                for (i in 1..<scale.toInt()){
                    mCanvas.drawCircle(Global.bezierCurve[i][0],
                        Global.bezierCurve[i][1], 2F, mPaint)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                mCanvas.drawBitmap(originalBitmap, 0f, 0f, null)
                mCanvas.drawBitmap(nodesBitmap!!, 0f, 0f, null)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Not implemented yet
            }
        })

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.my_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId){
            R.id.mnuListingNodes -> {
                intent = Intent(applicationContext, MainList::class.java)
                startActivity(intent)
            }
            R.id.mnuGenerateCurves -> {
                Toast.makeText(applicationContext, "You are already in listing view", Toast.LENGTH_LONG).show()
            }
        }

        return super.onOptionsItemSelected(item)
    }
}