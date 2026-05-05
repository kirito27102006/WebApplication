package org.example.jobsearchplatform.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.jobsearchplatform.dto.CompanyCreateRequest;
import org.example.jobsearchplatform.dto.CompanyResponse;
import org.example.jobsearchplatform.model.Company;
import org.example.jobsearchplatform.repository.CompanyRepository;
import org.example.jobsearchplatform.service.mapper.CompanyMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyMapper companyMapper;

    public CompanyResponse createCompany(CompanyCreateRequest request) {
        if (companyRepository.existsByName(request.getName())) {
            throw new IllegalArgumentException("Company with name " + request.getName() + " already exists");
        }

        Company company = companyMapper.toEntity(request);
        Company savedCompany = companyRepository.save(company);
        return companyMapper.toResponse(savedCompany);
    }

    public CompanyResponse findById(Long id) {
        return companyMapper.toResponse(getCompanyById(id));
    }

    public CompanyResponse findByName(String name) {
        Company company = companyRepository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with name: " + name));
        return companyMapper.toResponse(company);
    }

    public List<CompanyResponse> findAll() {
        return companyRepository.findAll().stream()
                .map(companyMapper::toResponse)
                .toList();
    }

    public List<CompanyResponse> findByIndustry(String industry) {
        return companyRepository.findByIndustry(industry).stream()
                .map(companyMapper::toResponse)
                .toList();
    }

    public List<CompanyResponse> searchByName(String keyword) {
        return companyRepository.searchByName(keyword).stream()
                .map(companyMapper::toResponse)
                .toList();
    }

    public CompanyResponse updateCompany(Long id, CompanyCreateRequest request) {
        Company company = getCompanyById(id);

        Optional.ofNullable(request.getName()).ifPresent(company::setName);
        company.setDescription(request.getDescription());
        company.setIndustry(request.getIndustry());
        company.setLocation(request.getLocation());
        company.setWebsite(request.getWebsite());
        company.setContactEmail(request.getContactEmail());
        company.setContactPhone(request.getContactPhone());

        Company updatedCompany = companyRepository.save(company);
        return companyMapper.toResponse(updatedCompany);
    }

    public CompanyResponse updateOwnedCompany(Long id, Long companyId, CompanyCreateRequest request) {
        ensureOwnedCompany(id, companyId);
        return updateCompany(id, request);
    }

    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }

    public void deleteOwnedCompany(Long id, Long companyId) {
        ensureOwnedCompany(id, companyId);
        deleteCompany(id);
    }

    private Company getCompanyById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Company not found with id: " + id));
    }

    private void ensureOwnedCompany(Long targetCompanyId, Long actorCompanyId) {
        if (actorCompanyId == null || !targetCompanyId.equals(actorCompanyId)) {
            throw new SecurityException("You can manage only your own company");
        }
    }
}
