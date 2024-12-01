package com.mreblan.cvservice.services.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.stereotype.Service;

import com.mreblan.cvservice.services.ZipService;

@Service
public class ZipServiceImpl implements ZipService {

    @Override
    public ZipOutputStream generateZipOutputStream(ByteArrayOutputStream outputStream) throws IOException {
        return new ZipOutputStream(outputStream);
    } 

    @Override
    public void appendZip(ZipOutputStream zos, byte[] data, String fileName) throws IOException {
        zos.putNextEntry(new ZipEntry(fileName + ".txt"));
        zos.write(data);
        zos.closeEntry();
    } 

}
