package ru.promoz.lovitkotlin

import RetrofitClient
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.promoz.lovitkotlin.Model.LoginResponse
import ru.promoz.lovitkotlin.Retrofit.PreAuthApi

class LoginActivity : AppCompatActivity() {

    internal lateinit var preApi: PreAuthApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar  = findViewById<Toolbar>(R.id.toolbar);
        setSupportActionBar(toolbar)

        //Инициализация API
        val retrofit = RetrofitClient.instance
        preApi = retrofit.create(PreAuthApi::class.java)

        //клик
        buttonSignUp.setOnClickListener {
            val contractNumber = loginContractNumber.text.toString().trim()
            val password = loginPassword.text.toString().trim()

            if(contractNumber.isEmpty()){
                loginContractNumber.error = "Введите номер договора"
                loginContractNumber.requestFocus()
                return@setOnClickListener
            }

            if(password.isEmpty()){
                loginPassword.error = "Введите пароль"
                loginPassword.requestFocus()
                return@setOnClickListener
            }


            preApi.authUser(contractNumber, password)
                .enqueue(object: Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>,response: Response<LoginResponse>) {
                        if (response.isSuccessful){
                            Toast.makeText(applicationContext, "Успешно", Toast.LENGTH_LONG).show()
                        }
                        else{
                            when(response.code()){
                                400 -> Toast.makeText(applicationContext, "Введены неверные данные", Toast.LENGTH_LONG).show()
                                404 -> Toast.makeText(applicationContext, "Страница не найдена", Toast.LENGTH_LONG).show()
                                500 -> Toast.makeText(applicationContext, "Внутренняя ошибка сервера", Toast.LENGTH_LONG).show()
                                else -> {
                                    Toast.makeText(applicationContext, "Непредвиденная ошибка", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    }
                })
        }
    }
}
