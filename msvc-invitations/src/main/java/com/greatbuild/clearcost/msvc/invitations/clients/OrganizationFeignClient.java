package com.greatbuild.clearcost.msvc.invitations.clients;

import com.greatbuild.clearcost.msvc.invitations.models.dtos.AddMemberDTO;
import com.greatbuild.clearcost.msvc.invitations.models.dtos.OrganizationDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "msvc-organizations", url = "${msvc.organizations.url:http://localhost:8002}")
public interface OrganizationFeignClient {

    @GetMapping("/api/organizations/{id}")
    OrganizationDTO getOrganizationById(@PathVariable("id") Long id);

    @PostMapping("/api/organizations/{id}/members")
    void addMember(@PathVariable("id") Long id, @RequestBody AddMemberDTO addMemberDTO);
}
