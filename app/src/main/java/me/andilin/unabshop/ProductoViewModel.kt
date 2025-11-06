package me.andilin.loginandregisterfirebaseauth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ProductoViewModel : ViewModel() {
    private val db = Firebase.firestore

    private val _productos = MutableStateFlow<List<Producto>>(emptyList())
    val productos: StateFlow<List<Producto>> = _productos

    private val _showDialog = MutableStateFlow(false)
    val showDialog: StateFlow<Boolean> = _showDialog

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = db.collection("productos").get().await()
                val productosList = result.map { doc ->
                    doc.toObject(Producto::class.java).copy(id = doc.id)
                }
                _productos.value = productosList
            } catch (e: Exception) {
                // Manejar error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun agregarProducto(producto: Producto) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                db.collection("productos").add(producto).await()
                cargarProductos()
                _showDialog.value = false
            } catch (e: Exception) {
                // Manejar error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun eliminarProducto(id: String) {
        viewModelScope.launch {
            try {
                db.collection("productos").document(id).delete().await()
                cargarProductos()
            } catch (e: Exception) {
                // Manejar error
            }
        }
    }

    fun mostrarDialog() {
        _showDialog.value = true
    }

    fun ocultarDialog() {
        _showDialog.value = false
    }
}