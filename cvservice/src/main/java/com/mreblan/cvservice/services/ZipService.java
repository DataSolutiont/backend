package com.mreblan.cvservice.services;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

public interface ZipService {

    ZipOutputStream generateZipOutputStream(ByteArrayOutputStream outputStream) throws IOException;
    void appendZip(ZipOutputStream zipOutputStream, byte[] data, String fileName) throws IOException;
}
