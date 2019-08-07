package com.zk.demo.service.impl;

import com.zk.demo.dto.AddDto;
import com.zk.demo.jni.AddJni;
import com.zk.demo.service.AddService;
import com.zk.demo.util.NativeLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AddImpl implements AddService {

    @Override
    public Integer add(AddDto addDto) {

        //根据操作系统判断，如果是linux系统则加载c++方法库
        String systemType = System.getProperty("os.name");
        String ext = (systemType.toLowerCase().indexOf("win") != -1) ? ".dll" : ".so";
        if(ext.equals(".so")) {
            try {
                NativeLoader.loader( "native" );
            } catch (Exception e) {
                System.out.println("加载so库失败");
            }
        }

        log.info("loaded");
        return AddJni.Add(addDto.getX(),addDto.getY());

    }
}
