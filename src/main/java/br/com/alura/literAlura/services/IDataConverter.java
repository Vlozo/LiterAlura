package br.com.alura.literAlura.services;

public interface IDataConverter {
    <T> T getData(String json, Class<T> tClass);
}
