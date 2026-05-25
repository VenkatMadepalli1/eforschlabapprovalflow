package com.eforsch.service;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.eforsch.entity.GroupEntity;
import com.eforsch.repository.GroupRepository;
import com.eforsch.util.GroupVO;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    // Convert Entity to VO
    private GroupVO entityToVO(GroupEntity groupEntity) {
        GroupVO groupVO = new GroupVO();
        groupVO.setGroupId(groupEntity.getGroupId());
        groupVO.setGroupName(groupEntity.getGroupName());
        groupVO.setGroupDesc(groupEntity.getGroupDesc());
        return groupVO;
    }

    // Convert VO to Entity
    private GroupEntity voToEntity(GroupVO groupVO) {
        GroupEntity groupEntity = new GroupEntity();
		if (groupVO.getGroupId() != null && groupVO.getGroupId() > 0) {
			groupEntity.setGroupId(groupVO.getGroupId());
		}
        groupEntity.setGroupName(groupVO.getGroupName());
        groupEntity.setGroupDesc(groupVO.getGroupDesc());
        return groupEntity;
    }

    public List<GroupVO> getAllGroups() {
        return groupRepository.findAll().stream()
                .map(this::entityToVO)
                .collect(Collectors.toList());
    }

    public GroupVO getGroupById(Long id) {
        return groupRepository.findById(id)
                .map(this::entityToVO)
                .orElse(null);
    }
    
    public GroupVO getGroupByName(String groupName) {
        GroupEntity groupEntity = groupRepository.findByGroupName(groupName);
        		if(groupEntity != null) {
                    return entityToVO(groupEntity);
        		}
        return null;
        }
    
   
    @Transactional
    public GroupVO createGroup(GroupVO groupVO) {
        GroupEntity groupEntity = voToEntity(groupVO);
        groupEntity = groupRepository.save(groupEntity);
        return entityToVO(groupEntity);
    }

    @Transactional
    public GroupVO updateGroup(GroupVO groupVO) {
        GroupEntity groupEntity = voToEntity(groupVO);
        groupEntity = groupRepository.save(groupEntity);
        return entityToVO(groupEntity);
    }

    @Transactional
    public void deleteGroup(Long id) {
        groupRepository.deleteById(id);
    }
}

