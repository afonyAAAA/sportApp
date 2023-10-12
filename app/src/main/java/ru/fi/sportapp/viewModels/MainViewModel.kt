package ru.fi.sportapp.viewModels

import android.content.Context
import android.util.Xml
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import nl.adaptivity.xmlutil.core.impl.multiplatform.Reader
import nl.adaptivity.xmlutil.deserialize
import nl.adaptivity.xmlutil.serialization.XML
import nl.adaptivity.xmlutil.util.SerializationProvider
import org.xml.sax.XMLReader
import ru.fi.sportapp.R
import ru.fi.sportapp.models.Casino
import java.net.ContentHandler

class MainViewModel(val context : Context) : ViewModel() {

    var casinos = mutableStateListOf<Casino>()

    init {
        getCasinos()
    }
    private fun getCasinos(){
        val xmlData = context.resources.getXml(R.xml.casinos).text
        val decodeCasinos = XML.decodeFromString<List<Casino>>(xmlData)
        casinos = decodeCasinos.toMutableStateList()
    }


}