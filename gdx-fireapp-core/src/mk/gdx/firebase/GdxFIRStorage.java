/*
 * Copyright 2017 mk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mk.gdx.firebase;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.File;

import mk.gdx.firebase.callbacks.DeleteCallback;
import mk.gdx.firebase.callbacks.DownloadCallback;
import mk.gdx.firebase.callbacks.UploadCallback;
import mk.gdx.firebase.distributions.StorageDistribution;
import mk.gdx.firebase.exceptions.PlatformDistributorException;
import mk.gdx.firebase.helpers.ImageHelper;

/**
 * Gets access to Firebase Analytics API in multi-modules.
 *
 * @see StorageDistribution
 * @see PlatformDistributor
 */
public class GdxFIRStorage extends PlatformDistributor<StorageDistribution> implements StorageDistribution
{

    private static GdxFIRStorage instance;

    /**
     * GdxFIRStorage protected constructor.
     * <p>
     * Instance of this class should be getting by {@link #instance()}
     * <p>
     * {@link PlatformDistributor#PlatformDistributor()}
     */
    protected GdxFIRStorage() throws PlatformDistributorException
    {
    }

    /**
     * @return Thread-safe singleton instance of this class.
     */
    public static GdxFIRStorage instance()
    {
        if (instance == null) {
            synchronized (GdxFIRAnalytics.class) {
                if (instance == null) {
                    try {
                        instance = new GdxFIRStorage();
                    } catch (PlatformDistributorException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void upload(FileHandle file, String path, UploadCallback callback)
    {
        platformObject.upload(file, path, callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void upload(byte[] data, String path, UploadCallback callback)
    {
        platformObject.upload(data, path, callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void download(String path, long bytesLimit, DownloadCallback<byte[]> callback)
    {
        platformObject.download(path, bytesLimit, callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void download(String path, File targetFile, DownloadCallback<File> callback)
    {
        platformObject.download(path, targetFile, callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void delete(String path, DeleteCallback callback)
    {
        platformObject.delete(path, callback);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StorageDistribution inBucket(String url)
    {
        return platformObject.inBucket(url);
    }


    /**
     * Downloads texture from Firebase storage.
     * <p>
     * Image is represented by TextureRegion because of need of size which should be power of two.<p>
     * Remember to dispose texture when you done:
     * <p>
     * {@code
     * region.getTexture().dispose();
     * }
     * <p>
     *
     * @param path     Path in FirebaseStorage bucket.
     * @param callback This callback will be call after image is downloaded.
     */
    public void downloadImage(String path, final DownloadCallback<TextureRegion> callback)
    {
        download(path, Long.MAX_VALUE, new DownloadCallback<byte[]>()
        {
            @Override
            public void onSuccess(final byte[] result)
            {
                Gdx.app.postRunnable(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        TextureRegion region = ImageHelper.createTextureFromBytes(result);
                        callback.onSuccess(region);
                    }
                });
            }

            @Override
            public void onFail(Exception e)
            {
                callback.onFail(e);
            }
        });
    }


    /**
     * {@inheritDoc}
     */
    @Override
    protected String getIOSClassName()
    {
        return "mk.gdx.firebase.ios.storage.Storage";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getAndroidClassName()
    {
        return "mk.gdx.firebase.android.storage.Storage";
    }
}
