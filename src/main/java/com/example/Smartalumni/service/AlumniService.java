package com.example.Smartalumni.service;

import com.example.Smartalumni.entity.User;
import com.example.Smartalumni.enums.Role;
import com.example.Smartalumni.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AlumniService {

    private final UserRepository userRepository;

    // ✅ Get all alumni
    public List<User> searchAlumni() {
        return userRepository.findByRole(Role.ALUMNI);
    }

    // ✅ Filter by Domain (jobTitle)
    public List<User> filterByDomain(String domain) {
        return userRepository
                .findByRoleAndJobTitleContainingIgnoreCase(Role.ALUMNI, domain);
    }

    // ✅ Filter by Company
    public List<User> filterByCompany(String company) {
        return userRepository
                .findByRoleAndCompanyContainingIgnoreCase(Role.ALUMNI, company);
    }

    // ✅ Filter by Location
    public List<User> filterByLocation(String location) {
        return userRepository
                .findByRoleAndLocationContainingIgnoreCase(Role.ALUMNI, location);
    }

    // ✅ Filter by Graduation Year (Batch)
    public List<User> filterByBatch(String year) {
        return userRepository
                .findByRoleAndGraduationYear(Role.ALUMNI, year);
    }

    // ✅ Filter by Skills
    public List<User> filterBySkills(String skill) {
        return userRepository
                .findByRoleAndSkillsContainingIgnoreCase(Role.ALUMNI, skill);
    }

    // ✅ Filter by Higher Education
    public List<User> filterByEducation(String education) {
        return userRepository
                .findByRoleAndHigherEducationContainingIgnoreCase(Role.ALUMNI, education);
    }

    // ✅ Filter by Department
    public List<User> filterByDepartment(String department) {
        return userRepository
                .findByRoleAndDepartmentContainingIgnoreCase(Role.ALUMNI, department);
    }

    // 🔥 NEW: COMBINED SEARCH (MAIN FEATURE)
    public List<User> searchAlumniAdvanced(
            String company,
            String department,
            String skill,
            String location,
            String batch,
            String education,
            String domain) {

        List<User> users = userRepository.findByRole(Role.ALUMNI);

        return users.stream()

                .filter(u -> company == null || u.getCompany() != null &&
                        u.getCompany().toLowerCase().contains(company.toLowerCase()))

                .filter(u -> department == null || u.getDepartment() != null &&
                        u.getDepartment().toLowerCase().contains(department.toLowerCase()))

                .filter(u -> skill == null || u.getSkills() != null &&
                        u.getSkills().toLowerCase().contains(skill.toLowerCase()))

                .filter(u -> location == null || u.getLocation() != null &&
                        u.getLocation().toLowerCase().contains(location.toLowerCase()))

                .filter(u -> batch == null || u.getGraduationYear() != null &&
                        u.getGraduationYear().equalsIgnoreCase(batch))

                .filter(u -> education == null || u.getHigherEducation() != null &&
                        u.getHigherEducation().toLowerCase().contains(education.toLowerCase()))

                .filter(u -> domain == null || u.getJobTitle() != null &&
                        u.getJobTitle().toLowerCase().contains(domain.toLowerCase()))

                .toList();
    }
}