package com.zk.demo.controller;


import com.zk.demo.dto.AddDto;
import com.zk.demo.service.AddService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class Add {

    @Autowired
    private AddService addService;

    @PostMapping("/add")
    public Integer add(@RequestBody @Valid AddDto addDto){
        return addService.add(addDto);
    }
}
