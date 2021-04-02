package com.udacity.asteroidradar.api

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.lang.reflect.Type

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention()
internal annotation class Json

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
@Retention()
internal annotation class Scalar

class ConverterScalarOrJson: Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
        annotations?.forEach { annotation ->
            when (annotation.annotationClass.simpleName.toString()) {
                Scalar::class.java.simpleName.toString() -> return ScalarsConverterFactory.create().responseBodyConverter(type, annotations, retrofit)
                Json::class.java.simpleName.toString() -> return MoshiConverterFactory.create(moshi).responseBodyConverter(type, annotations, retrofit)
                else -> null
            }
        }
        return MoshiConverterFactory.create(moshi).responseBodyConverter(type, annotations, retrofit)
    }

    companion object {
        fun create() = ConverterScalarOrJson()
    }
}