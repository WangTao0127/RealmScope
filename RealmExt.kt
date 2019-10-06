package cn.scent.common.realm

import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmQuery
import io.realm.RealmResults
import java.lang.Exception
import java.lang.RuntimeException
import java.util.*

typealias QueryForWhereFun<T> = RealmQuery<out T>.()-> Unit
class RealmExt (val realm: Realm){
    companion object{
        fun getDefaultInstance() = RealmExt(Realm.getDefaultInstance())
    }

    fun <T: RealmModel> findOrCreate(clazz:Class<T>,primaryKeyName:String,primaryKey:Any): T {
        return findFirst(clazz){equalTo(primaryKeyName, primaryKey)}?:createObject(clazz,primaryKey)!!
    }
    fun <T: RealmModel> findFirst(clazz:Class<T>, whereFun: QueryForWhereFun<T>?=null): T? {
        return createQuery(clazz,whereFun).findFirst()
    }
    fun <T:RealmModel> findAll(clazz:Class<T>,whereFun: QueryForWhereFun<T>?=null): RealmResults<T>? {
        return createQuery(clazz,whereFun).findAll()
    }
    fun <T:RealmModel> count(clazz:Class<T>,whereFun: QueryForWhereFun<T>?=null): Long {
        return createQuery(clazz,whereFun).count()
    }
    fun <T:RealmModel> save(data:T):Boolean{
        checkRealmClose()
        try {
            realm.beginTransaction()
            realm.copyToRealmOrUpdate(data)
            realm.commitTransaction()
            return true
        }catch (e: Exception){
            realm.cancelTransaction()
        }
        realm.beginTransaction()
        return false
    }
    fun <T:RealmModel> createObject(clazz: Class<T>,primaryKey:Any) = realm.createObject(clazz,primaryKey)
    fun beginTransaction()= realm.beginTransaction()
    fun commitTransaction()= realm.commitTransaction()
    fun cancelTransaction()= realm.cancelTransaction()

    private fun checkRealmClose() {
        if(realm.isClosed)throw RuntimeException("realm is Close")
    }

    fun <T:RealmModel> saveAll(data:List<T>):Boolean{
        checkRealmClose()
        try {
            realm.beginTransaction()
            realm.copyToRealmOrUpdate(data)
            realm.commitTransaction()
            return true
        }catch (e: Exception){
            realm.cancelTransaction()
        }
        return false
    }

    private fun <T:RealmModel> createQuery(clazz:Class<T>, whereFun: QueryForWhereFun<T>?=null): RealmQuery<T> {
        return realm.where(clazz).apply {whereFun?.let{this.it()}}
    }
    fun close(){

        realm.close()
    }

    fun executeTransaction(block: () -> Unit){
        realm.beginTransaction()
        try {
            block.invoke()
            realm.commitTransaction()
        }catch (e:Exception){
            realm.cancelTransaction()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            throw e
        }
    }
}

fun <E> RealmQuery<E>.equalTo(primaryKeyName: String, primaryKey: Any) {
    when(primaryKey){
        is String->equalTo(primaryKeyName,primaryKey)
        is Short->equalTo(primaryKeyName,primaryKey)
        is Int->equalTo(primaryKeyName,primaryKey)
        is Long->equalTo(primaryKeyName,primaryKey)
        is Float->equalTo(primaryKeyName,primaryKey)
        is Double->equalTo(primaryKeyName,primaryKey)
        is Date ->equalTo(primaryKeyName,primaryKey)
        else->throw IllegalArgumentException("primaryKey 不支持的类型${primaryKey.javaClass.name}")
    }
}

