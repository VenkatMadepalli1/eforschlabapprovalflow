package com.eforsch.util;

import java.util.Base64;

import com.eforsch.entity.Inventory;

public class InventoryMapper {

    // VO -> Entity
    public static Inventory fromVOToEntity(InventoryVO vo) {
        if (vo == null) return null;

        Inventory entity = new Inventory();
        entity.setProductId(vo.getProductId());
        entity.setProductname(vo.getProductname());
        entity.setCatalogue(vo.getCatalogue());
        entity.setCompanyname(vo.getCompanyname());
        entity.setQuantity(vo.getQuantity());
        entity.setGroupName(vo.getGroupName());
        entity.setCompanyinternalno(vo.getCompanyinternalno());
        entity.setSapmaterialno(vo.getSapmaterialno());
        entity.setWeightvolsubqty(vo.getWeightvolsubqty());
        entity.setBudgetno(vo.getBudgetno());
        entity.setConcentration(vo.getConcentration());
        entity.setRemarks(vo.getRemarks());
        entity.setOrderdate(vo.getOrderdate());
        entity.setExpirydate(vo.getExpirydate());
        entity.setAddedby(vo.getAddedby());
        entity.setShared(vo.isShared());
        entity.setFileName(vo.getFileName());
        entity.setFileType(vo.getFileType());
        entity.setPrice(vo.getPrice());

        if (vo.getFileContent() != null) {
            entity.setFileContent(vo.getFileContent());
        }

        return entity;
    }

 // Entity -> VO
    public static InventoryVO fromEntityToVO(Inventory entity, boolean includeFileContent) {
        if (entity == null) return null;

        InventoryVO vo = new InventoryVO();
        vo.setProductId(entity.getProductId());
        vo.setProductname(entity.getProductname());
        vo.setCatalogue(entity.getCatalogue());
        vo.setCompanyname(entity.getCompanyname());
        vo.setQuantity(entity.getQuantity());
        vo.setGroupName(entity.getGroupName());
        vo.setCompanyinternalno(entity.getCompanyinternalno());
        vo.setSapmaterialno(entity.getSapmaterialno());
        vo.setWeightvolsubqty(entity.getWeightvolsubqty());
        vo.setBudgetno(entity.getBudgetno());
        vo.setConcentration(entity.getConcentration());
        vo.setRemarks(entity.getRemarks());
        vo.setOrderdate(entity.getOrderdate());
        vo.setExpirydate(entity.getExpirydate());
        vo.setAddedby(entity.getAddedby());
        vo.setShared(entity.isShared());
        vo.setPrice(entity.getPrice());

        vo.setFileName(entity.getFileName());
        vo.setFileType(entity.getFileType());

        if (includeFileContent && entity.getFileContent() != null) {
           // vo.setFileContent(entity.getFileContent());
        }

        return vo;
    }
}