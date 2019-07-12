package com.example.pepper_onlysticker

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.estimote.internal_plugins_api.scanning.BluetoothScanner
import com.estimote.internal_plugins_api.scanning.ScanHandler
import com.estimote.mustard.rx_goodness.rx_requirements_wizard.RequirementsWizardFactory
import com.estimote.scanning_plugin.api.EstimoteBluetoothScannerFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {


    private lateinit var bluetoothScanner: BluetoothScanner
    private var scanHandle: ScanHandler? = null
    private var objectList = ArrayList<String>()
    private var idList = arrayListOf<String>()
    private var objectName = mutableListOf<String>()
    lateinit var currentTime: String
    lateinit var clientSticker: ClientSocket
    private val ip = "130.251.13.109"
    private val port = 8080

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        RequirementsWizardFactory.createEstimoteRequirementsWizard().fulfillRequirements(
            this,
            onRequirementsFulfilled = {
                Log.d("Beacons", "onRequirementsFulfilled")
                startSticker()
            },
            onRequirementsMissing = {},
            onError = {}
        )
    }

    fun startSticker() {
        bluetoothScanner =
            EstimoteBluetoothScannerFactory(applicationContext).getSimpleScanner()
        scanHandle = bluetoothScanner
            .estimoteNearableScan()
            .withBalancedPowerMode()
            .withOnPacketFoundAction {
                // has id.rssi if isn't correct can use this to define what is Tx??
                /*
                val idList = ArrayList<String>()
                idList.add(it.deviceId)
                val store = mutableMapOf<String, Int>()
                //val sticker = StickerUtils.stickers
                //val store = HashMap<String, Int>()
                idList.forEach { store[it] = (store[it] ?: 0) + 1 }
                store.mapValues {
                    if (it.value>=3){

                    }

                }*/

                // set sticker advertise frequency = 0.1s
                // using window function then every 5 ids as a set, select >=3 as found object, clear this 10
                val stickerMap = mapOf("71417EE260BBA6F3" to "phone","C15DE122BF2973DF" to "TV Monitor","C09A634D9870AEDC" to "book")
                idList.add(it.deviceId)
                Log.d("old idlist", "$idList")
                if (idList.size >= 5) {
                    // windowedIdtry is ListMapof, [{空or id=\次数},{}...],each {} from one window
                    //val windowedId = idList.windowed(size = 10, step =10).toList()
                    // what happened if has more than one ??
                    val windowedIdtry = idList.windowed(size = 5, step = 5) { window ->
                        window.groupingBy { it }.eachCount().filter { it.value >=2 }

                    }
                    Log.d("id", "$windowedIdtry" )
                    // windowedIdtry.last() -> a map, map.keys -> a set[](similar with list but delete repeat elements)
                    val id = windowedIdtry.last().keys
                    // use for will give many times results

                    for (i in id){
                        //objectName.add(stickerMap[i]!!)
                        val obj = stickerMap[i].toString()!!
                        Log.d("result", "$obj" )
                        clientSticker = ClientSocket(ip, port,"pepper find $obj")
                        clientSticker.openClient()

                    }


                    //val sendMsg = "$id"
                    //clientSticker = ClientSocket(ip, port, "$objectName")
                    //clientSticker.openClient()


                    //Log.d("result", "${windowedIdtry.last()}" )

                    //Log.d("result", id )
                    //val obj = stickerMap[id]
                    //Log.d("try", obj )



                    //Log.d("result", "${windowedIdtry.last().}")


                    /*val store = mutableMapOf<String, Int>()
                    idList.take(10).forEach { store[it] = (store[it] ?: 0) + 1 }
                    store.mapValues {
                        if (it.value>=3){
                            for (sticker in StickerUtils.stickers)
                                if (sticker.stickerId == it.key) {
                                    Log.d("key","${sticker.attachedObject}")

                                }




                        }

                    }
                    idList.drop(10)
                    Log.d("new idlist","$idList")*/


                }


                /*for (sticker in StickerUtils.stickers)
                    if (sticker.stickerId == it.deviceId) {



                        //currentTime = getCurrentDateTime().toString("yyyy/MM/dd HH:mm:ss")
                        //clientSticker = ClientSocket(ip, port, "At $currentTime Pepper find ${sticker.attachedObject}")
                        //clientSticker.openClient()
                        //Log.d("object", "At $currentTime Pepper find ${sticker.attachedObject}")

                        objectList.add(sticker.attachedObject)
                        // pepper need to decrease size
                        if (objectList.size ==6){
                            objectName = null
                            objectName = objectList.distinct()
                            tv_object.text = sticker.attachedObject
                            objectList.clear()
                            // open client once detect object worked for locolization otherwise put this in main
                            currentTime = getCurrentDateTime().toString("yyyy/MM/dd HH:mm:ss")
                            clientSticker = ClientSocket(ip, port, "$currentTime Pepper find $objectName")
                            clientSticker.openClient()
                        }
                    }*/

            }
            .withOnScanErrorAction { Log.e("STICKER", "scan failed: $it") }
            .start()

    }


    override fun onDestroy() {
        super.onDestroy()
        scanHandle?.stop()
    }
}


