package com.lambdaschool.starthere.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.annotation.processing.Generated;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

// User is considered the parent entity

@ApiModel(value = "User", description = "The User Entity")
@Entity
@Table(name = "users")
public class User extends Auditable
{


    @ApiModelProperty(name = "userid", value = "Primary key for user", required =true, example ="1")
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long userid;

    @ApiModelProperty(name = "username", value = "User Name", required = true, example = "John Doe")
    @Column(nullable = false,
            unique = true)
    private String username;

    @ApiModelProperty(name = "password", value = "Password", required = true, example = "password")
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @ApiModelProperty (name = "phoneNumber", value = "Phone Number", required = true, example = "555-555-5555")
    @Column(nullable = false,
            unique = false)
    private String phonenumber;

    @ApiModelProperty(name = "industrytype", value = "Industry Type", required = true, example = "Finance")
    @Column(nullable = false)
    private String industrytype;

    @OneToMany(mappedBy = "user",
            cascade = CascadeType.ALL)
    @JsonIgnoreProperties("user")
    private List<UserTypes> userTypes = new ArrayList<>();

    @OneToMany(mappedBy = "user",
               cascade = CascadeType.ALL)
    @JsonIgnoreProperties("user")
    private List<UserRoles> userRoles = new ArrayList<>();

    @OneToMany(mappedBy = "user",
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    @JsonIgnoreProperties("user")
    private List<Question> questions = new ArrayList<>();

    public User()
    {
    }

    public User(String username, String password, String phonenumber, String industrytype, List<UserTypes> userTypes, List<UserRoles> userRoles)
    {
        setUsername(username);
        setPassword(password);
        setPhonenumber(phonenumber);
        setIndustrytype(industrytype);
        this.userTypes = userTypes;
        for (UserTypes ut : userTypes)
        {
            ut.setUser(this);
        }
        for (UserRoles ur : userRoles)
        {
            ur.setUser(this);
        }
        this.userRoles = userRoles;
    }

    public long getUserid()
    {
        return userid;
    }

    public void setUserid(long userid)
    {
        this.userid = userid;
    }


    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        this.password = passwordEncoder.encode(password);
    }

    public void setPasswordNoEncrypt(String password)
    {
        this.password = password;
    }

    public String getIndustrytype() {
        return industrytype;
    }

    public void setIndustrytype(String industrytype) {
        this.industrytype = industrytype;
    }

    public List<UserTypes> getUserTypes()
    {
        return userTypes;
    }

    public void setUserTypes(List<UserTypes> userTypes)
    {
        this.userTypes = userTypes;
    }

    public List<UserRoles> getUserRoles()
    {
        return userRoles;
    }

    public void setUserRoles(List<UserRoles> userRoles)
    {
        this.userRoles = userRoles;
    }

    public List<Question> getQuestions()
    {
        return questions;
    }

    public void setQuestions(List<Question> questions)
    {
        this.questions = questions;
    }

    public List<SimpleGrantedAuthority> getAuthority()
    {
        List<SimpleGrantedAuthority> rtnList = new ArrayList<>();

        for (UserRoles r : this.userRoles)
        {
            String myRole = "ROLE_" + r.getRole().getName().toUpperCase();
            rtnList.add(new SimpleGrantedAuthority(myRole));
        }

        return rtnList;
    }

}
