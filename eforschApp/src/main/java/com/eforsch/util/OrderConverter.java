package com.eforsch.util;

import java.util.ArrayList;
import java.util.Arrays;

import com.eforsch.entity.Order;

public final class OrderConverter {


 public static OrderVO fromEntityToVO(Order e) {
     if (e == null) return null;
     OrderVO vo = new OrderVO();

     vo.setOrderId(e.getOrderId());
     vo.setProductId(e.getProductId());
     vo.setProductName(e.getProductName());
     vo.setCatalogue(e.getCatalogue());
     vo.setCompanyName(e.getCompanyName());
     vo.setQuantity(e.getQuantity());
     vo.setBudgetno(e.getBudgetno());
     vo.setPrice(e.getPrice());

     vo.setSafetydatasheet(e.getSafetydatasheet());
     vo.setExpiryDate(e.getExpiryDate());

     vo.setCompanyinternalno(e.getCompanyInternalNo());
     vo.setSapmaterialno(e.getSapMaterialNo());
     vo.setWeightvolsubqty(e.getWeightVolSubQty());

     vo.setOrderdate(e.getOrderDate());
     vo.setOrderedby(e.getOrderedBy());
     vo.setConcentration(e.getConcentration());

     vo.setRemarks(e.getRemarks());
     vo.setCasNumber(e.getCasNumber());
     vo.setHazardousSubstance(e.getHazardousSubstance());
     vo.setCmrSubstance(e.getCmrSubstance());
     vo.setSkinResorptive(e.getSkinResorptive());
     vo.setAttachment(e.getSafetydatasheet());

     vo.setGhsSymbols(e.getGhsSymbols() != null ? new ArrayList<>(e.getGhsSymbols()) : null);
     vo.setGhsSignalWord(e.getGhsSignalWord() != null ? new ArrayList<>(e.getGhsSignalWord()) : null);
     vo.setGhsCheckbox(e.getGhsCheckbox());
     vo.sethPhrases(e.gethPhrases());
     vo.setpPhrases(e.getpPhrases());
     vo.setSubstitutionCheck(e.getSubstitutionCheck());
     vo.setStorageLocation(e.getStorageLocation());
     vo.setSubstitutionOption(e.getSubstitutionOption());
     
     vo.setInventoryType(e.getInventoryType());

     vo.setAdminApproved(e.getAdminApproved());
     vo.setLabApproved(e.getLabApproved());
     vo.setAdminApprovalStatusDate(e.getAdminApprovalStatusDate());
     vo.setLabApprovalStatusDate(e.getLabApprovalStatusDate());

     vo.setAdminName(e.getAdminName());
     vo.setUserName(e.getUserName());
     vo.setStatus(e.getStatus());

     vo.setFileContent(e.getFileContent() != null ? Arrays.copyOf(e.getFileContent(), e.getFileContent().length) : null);

     vo.setCreatedAt(e.getCreatedAt());
     vo.setUpdatedAt(e.getUpdatedAt());
     vo.setCreatedBy(e.getCreatedBy());
     vo.setUpdatedBy(e.getUpdatedBy());
     vo.setGroupName(e.getGroupName());

     return vo;
 }

 /** If target is null, a new Order is created. Otherwise fields are copied into target. */
 public static Order fromVOToEntity(OrderVO vo, Order target) {
     if (vo == null) return null;
     Order e = (target != null) ? target : new Order();

     e.setOrderId(vo.getOrderId());
     e.setProductName(vo.getProductName());
     e.setCatalogue(vo.getCatalogue());
     e.setCompanyName(vo.getCompanyName());
     e.setQuantity(vo.getQuantity());
     e.setBudgetno(vo.getBudgetno());
     e.setPrice(vo.getPrice());

     e.setSafetydatasheet(vo.getSafetydatasheet());
     e.setExpiryDate(vo.getExpiryDate());
     
     e.setInventoryType(vo.getInventoryType());

     e.setCompanyInternalNo(vo.getCompanyinternalno());
     e.setSapMaterialNo(vo.getSapmaterialno());
     e.setWeightVolSubQty(vo.getWeightvolsubqty());

     e.setOrderDate(vo.getOrderdate());
     e.setOrderedBy(vo.getOrderedby());
     e.setConcentration(vo.getConcentration());

     e.setRemarks(vo.getRemarks());
     e.setCasNumber(vo.getCasNumber());
     e.setHazardousSubstance(vo.getHazardousSubstance());
     e.setCmrSubstance(vo.getCmrSubstance());
     e.setSkinResorptive(vo.getSkinResorptive());

     e.setGhsSymbols(vo.getGhsSymbols() != null ? new ArrayList<>(vo.getGhsSymbols()) : null);
     e.setGhsSignalWord(vo.getGhsSignalWord() != null ? new ArrayList<>(vo.getGhsSignalWord()) : null);
     e.setGhsCheckbox(vo.getGhsCheckbox());
     e.sethPhrases(vo.gethPhrases());
     e.setpPhrases(vo.getpPhrases());
     e.setSubstitutionCheck(vo.getSubstitutionCheck());
     e.setStorageLocation(vo.getStorageLocation());
     e.setSubstitutionOption(vo.getSubstitutionOption());

     e.setAdminApproved(vo.getAdminApproved());
     e.setLabApproved(vo.getLabApproved());
     e.setAdminApprovalStatusDate(vo.getAdminApprovalStatusDate());
     e.setLabApprovalStatusDate(vo.getLabApprovalStatusDate());

     e.setAdminName(vo.getAdminName());
     e.setUserName(vo.getUserName());
     e.setStatus(vo.getStatus());

     e.setFileContent(vo.getFileContent() != null ? Arrays.copyOf(vo.getFileContent(), vo.getFileContent().length) : null);

     e.setCreatedAt(vo.getCreatedAt());
     e.setUpdatedAt(vo.getUpdatedAt());
     e.setCreatedBy(vo.getCreatedBy());
     e.setUpdatedBy(vo.getUpdatedBy());
     e.setGroupName(vo.getGroupName());
     e.setProductId(vo.getProductId());

     return e;
 }
}
