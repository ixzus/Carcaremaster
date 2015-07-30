package com.lazycare.carcaremaster.image;

import android.graphics.Bitmap;

import com.facebook.imagepipeline.request.BaseRepeatedPostProcessor;

import java.util.ArrayList;

/**
 * 添加接口集合
 */
public class TuoFrescoProcessor extends BaseRepeatedPostProcessor {
    private ArrayList<ProcessorInterface> processorList = new ArrayList<>();

    public TuoFrescoProcessor addProcessor(ProcessorInterface processor) {
        this.processorList.add(processor);
        return this;
    }

    @Override
    public void process(Bitmap bitmap) {
        for (int i = 0; i < processorList.size(); i++) {
            processorList.get(i).process(bitmap);
        }
    }
}