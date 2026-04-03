package com.church.anglican.backend.controllers;

import com.church.anglican.backend.dto.identity.AddGroupMemberRequest;
import com.church.anglican.backend.dto.identity.CreateGroupRequest;
import com.church.anglican.backend.dto.identity.GroupMemberResponse;
import com.church.anglican.backend.dto.identity.GroupResponse;
import com.church.anglican.backend.dto.identity.UpdateGroupMemberRequest;
import com.church.anglican.backend.entities.identity.Church;
import com.church.anglican.backend.entities.identity.Group;
import com.church.anglican.backend.entities.identity.GroupMember;
import com.church.anglican.backend.entities.identity.Person;
import com.church.anglican.backend.services.identity.GroupMemberService;
import com.church.anglican.backend.services.identity.GroupService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
public class GroupController {

    private final GroupService groupService;
    private final GroupMemberService groupMemberService;

    public GroupController(GroupService groupService, GroupMemberService groupMemberService) {
        this.groupService = groupService;
        this.groupMemberService = groupMemberService;
    }

    @PostMapping
    public ResponseEntity<GroupResponse> create(@Valid @RequestBody CreateGroupRequest request) {
        Group group = new Group();
        Church church = new Church();
        church.setId(request.getChurchId());
        group.setChurch(church);
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setType(request.getType());
        group.setStatus(request.getStatus() != null ? request.getStatus() : Group.GroupStatus.ACTIVE);
        Group saved = groupService.create(group);
        return ResponseEntity.ok(toGroupResponse(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Group> getById(@PathVariable UUID id) {
        Group group = groupService.findById(id);
        return ResponseEntity.ok(group);
    }

    @GetMapping
    public ResponseEntity<Page<GroupResponse>> list(
            @RequestParam UUID churchId,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Group.GroupStatus status,
            @RequestParam(required = false) Group.GroupType type,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<Group> groups = groupService.search(churchId, q, status, type, pageable);
        return ResponseEntity.ok(mapGroupPage(groups));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GroupResponse> update(@PathVariable UUID id, @Valid @RequestBody CreateGroupRequest request) {
        Group group = new Group();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setType(request.getType());
        group.setStatus(request.getStatus() != null ? request.getStatus() : Group.GroupStatus.ACTIVE);
        return ResponseEntity.ok(toGroupResponse(groupService.update(id, group)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        groupService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{groupId}/members")
    public ResponseEntity<GroupMember> addMember(@PathVariable UUID groupId, @Valid @RequestBody AddGroupMemberRequest request) {
        Group group = new Group();
        group.setId(groupId);
        Person person = new Person();
        person.setId(request.getPersonId());
        GroupMember member = new GroupMember();
        member.setGroup(group);
        member.setPerson(person);
        member.setStatus(request.getStatus());
        member.setDuesStatus(request.getDuesStatus());
        member.setJoinedAt(request.getJoinedAt());
        member.setLeftAt(request.getLeftAt());
        member.setDuesPaidThrough(request.getDuesPaidThrough());
        return ResponseEntity.ok(groupMemberService.add(member));
    }

    @GetMapping("/{groupId}/members")
    public ResponseEntity<Page<GroupMemberResponse>> listMembers(
            @PathVariable UUID groupId,
            @RequestParam(required = false) UUID personId,
            @RequestParam(required = false) GroupMember.MemberStatus status,
            @RequestParam(required = false) GroupMember.DuesStatus duesStatus,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<GroupMember> members = groupMemberService.list(groupId, personId, status, duesStatus, pageable);
        return ResponseEntity.ok(mapGroupMemberPage(members));
    }

    @PutMapping("/{groupId}/members/{memberId}")
    public ResponseEntity<GroupMemberResponse> updateMember(
            @PathVariable UUID groupId,
            @PathVariable UUID memberId,
            @Valid @RequestBody UpdateGroupMemberRequest request
    ) {
        GroupMember member = new GroupMember();
        member.setStatus(request.getStatus());
        member.setDuesStatus(request.getDuesStatus());
        member.setJoinedAt(request.getJoinedAt());
        member.setLeftAt(request.getLeftAt());
        member.setDuesPaidThrough(request.getDuesPaidThrough());
        return ResponseEntity.ok(toGroupMemberResponse(groupMemberService.update(groupId, memberId, member)));
    }

    @DeleteMapping("/{groupId}/members/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable UUID groupId, @PathVariable UUID memberId) {
        groupMemberService.delete(groupId, memberId);
        return ResponseEntity.noContent().build();
    }

    private Page<GroupResponse> mapGroupPage(Page<Group> page) {
        return new PageImpl<>(
                page.getContent().stream().map(this::toGroupResponse).toList(),
                page.getPageable(),
                page.getTotalElements()
        );
    }

    private GroupResponse toGroupResponse(Group group) {
        GroupResponse response = new GroupResponse();
        response.setId(group.getId());
        response.setChurchId(group.getChurch() != null ? group.getChurch().getId() : null);
        response.setName(group.getName());
        response.setDescription(group.getDescription());
        response.setType(group.getType());
        response.setStatus(group.getStatus());
        response.setCreatedAt(group.getCreatedAt());
        response.setUpdatedAt(group.getUpdatedAt());
        return response;
    }

    private Page<GroupMemberResponse> mapGroupMemberPage(Page<GroupMember> page) {
        return new PageImpl<>(
                page.getContent().stream().map(this::toGroupMemberResponse).toList(),
                page.getPageable(),
                page.getTotalElements()
        );
    }

    private GroupMemberResponse toGroupMemberResponse(GroupMember member) {
        GroupMemberResponse response = new GroupMemberResponse();
        response.setId(member.getId());
        response.setGroupId(member.getGroup() != null ? member.getGroup().getId() : null);
        response.setPersonId(member.getPerson() != null ? member.getPerson().getId() : null);
        response.setPersonName(member.getPerson() != null ? member.getPerson().getFullName() : null);
        response.setPersonEmailAddress(member.getPerson() != null ? member.getPerson().getEmailAddress() : null);
        response.setPersonPhoneNumber(member.getPerson() != null ? member.getPerson().getPhoneNumber() : null);
        response.setStatus(member.getStatus());
        response.setDuesStatus(member.getDuesStatus());
        response.setJoinedAt(member.getJoinedAt());
        response.setLeftAt(member.getLeftAt());
        response.setDuesPaidThrough(member.getDuesPaidThrough());
        response.setCreatedAt(member.getCreatedAt());
        response.setUpdatedAt(member.getUpdatedAt());
        return response;
    }
}
