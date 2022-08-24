package org.apache.rat.mp;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import org.apache.rat.api.Document;
import org.apache.rat.api.MetaData;
import org.apache.rat.api.RatException;
import org.apache.rat.document.impl.DocumentImplUtils;
import org.apache.rat.report.IReportable;
import org.apache.rat.report.RatReport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;


/**
 * Implementation of IReportable that traverses over a set of files.
 */
class FilesReportable implements IReportable
{
    private final File basedir;

    private final String[] files;

    FilesReportable( File basedir, String[] files )
            throws IOException
    {
        final File currentDir = new File( System.getProperty( "user.dir" ) ).getCanonicalFile();
        System.out.println("CurrenDirFilesReportable:" + currentDir.getAbsolutePath());
        if(currentDir.getAbsolutePath().contains("schema2template")){
            System.err.println("starting debugging");
        }

        final File f = basedir.getCanonicalFile();
        System.out.println("baseDir-A:" + f.getAbsolutePath());
        if ( currentDir.equals( f ) )
        {
            this.basedir = null;
        }
        else
        {
            this.basedir = basedir;
        }
        System.out.println("baseDir-B:" + this.basedir);
        this.files = files;
    }

    public void run( RatReport report ) throws RatException
    {
        FileDocument document = new FileDocument();
        for (String file : files) {
            if(basedir == null){
                System.out.println("*******The baseDir:" + null + " and the file path is '" + file + "'!");
            }else{
                System.out.println("*******The baseDir:" + basedir.getAbsolutePath() + " and the file path is '" + file + "'!");
            }
            System.out.println("*******UserDir:'" + System.getProperty( "user.dir" ) + "'!");
            File newF = new File(basedir, file);
            System.out.println("*******newF:'" + newF.getAbsolutePath() + "'!");
            document.setFile(newF);
            document.getMetaData().clear();
            report.report(document);
        }
    }

    private class FileDocument implements Document
    {
        private File file;
        private final MetaData metaData = new MetaData();
        
        void setFile( File file )
        {
            this.file = file;
        }

        public boolean isComposite() {
            return DocumentImplUtils.isZip(file);
        }

        public Reader reader() throws IOException
        {
            final InputStream in = new FileInputStream( file );
            return new InputStreamReader( in );
        }

        public String getName()
        {
            return DocumentImplUtils.toName( file );
        }

        public MetaData getMetaData() {
            return metaData;
        }
        
        public InputStream inputStream() throws IOException {
            return new FileInputStream(file);
        }
        
        @Override
        public String toString() {
            return "File:" + file.getAbsolutePath();
        }
    }
}
