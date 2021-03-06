package com.example.CRM.Rights.RightsChart;

import com.example.CRM.Rights.*;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/rights_chart")
public class RightController {
    private final RightsRepository rightsRepository;
    private final RightsModelAssembler rightsModelAssembler;

    public RightController(RightsRepository rightsRepository, RightsModelAssembler rightsModelAssembler) {
        this.rightsRepository = rightsRepository;
        this.rightsModelAssembler = rightsModelAssembler;
    }

    @GetMapping("/{id}")
    EntityModel<RightChart> one(@PathVariable int id) {

        RightChart rightChart = rightsRepository.findById(id)//
                .orElseThrow(() -> new RightsNotFoundException(id));

        return rightsModelAssembler.toModel(rightChart);
    }
    @GetMapping
    CollectionModel<EntityModel<RightChart>> all() {

        List<EntityModel<RightChart>> rightChart = rightsRepository.findAll().stream() //
                .map(rightsModelAssembler::toModel) //
                .collect(Collectors.toList());

        return CollectionModel.of(rightChart, linkTo(methodOn(RightController.class).all()).withSelfRel());
    }

    @PostMapping
    ResponseEntity<?> addRightsChart(@RequestBody RightChart rightChart) {

        EntityModel<RightChart> entityModel = rightsModelAssembler.toModel(rightsRepository.save(rightChart));

        return ResponseEntity //
                .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()) //
                .body(entityModel);
    }
    @PutMapping("/{id}")
    ResponseEntity<?> updateRightsChart(@RequestBody RightChart rightChart , @PathVariable int id ){
        RightChart rightChartUpdate = rightsRepository.findById(id)//
            .map(rights -> {
                rights.setRightsCode(rightChart.getRightsCode());
                rights.setUserCode(rightChart.getUserCode());
                return rightsRepository.save(rights);
            })//
            .orElseGet(()->{
                rightChart.setRightsChartId(id);
                return rightsRepository.save(rightChart);
            });
        EntityModel<RightChart> rightChartEntityModel = rightsModelAssembler.toModel(rightChartUpdate);
        return ResponseEntity//
            .created(rightChartEntityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())//
            .body(rightChartEntityModel);
    }

}
