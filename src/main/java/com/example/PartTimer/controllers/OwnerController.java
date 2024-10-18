package com.example.PartTimer.controllers;

import com.example.PartTimer.dto.OwnerDTO;
import com.example.PartTimer.entities.Owner;
import com.example.PartTimer.services.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/owner")
public class OwnerController {
//
//    @Autowired
//    private OwnerService ownerService;
//
//    @PostMapping("/add")
//    public Owner addEmployee(@RequestBody Owner owner) {
//        return ownerService.saveOwner(owner);
//    }
//
//    @GetMapping("/get")
//    public List<OwnerDTO> getAllOwners() {
//        return ownerService.getOwners();
//    }
}
