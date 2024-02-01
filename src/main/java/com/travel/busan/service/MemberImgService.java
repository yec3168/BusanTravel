package com.travel.busan.service;

import com.travel.busan.entity.Member;
import com.travel.busan.entity.MemberImg;
import com.travel.busan.repository.MemberImgRepository;
import com.travel.busan.repository.MemberRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
public class MemberImgService {
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberImgRepository memberImgRepository;

    @Value("${imgSave.location}")
    private String uploadImage;

    @Autowired
    private FileService fileService;

    @Transactional
    public void upload(MemberImg memberImg, MultipartFile multipartFile, Member saveMember){
        // 파일 이름, url 재설정.

        String saveFileNm = renameFile(multipartFile);

        String saveUrl = "/image/members"+"/"+saveFileNm; //db url
        String checkFolder = uploadImage +"/members"; //mk dir
        String uploadUrl = checkFolder+"/"+saveFileNm; //file save

        //상품 이미지 저장.
        memberImg.updateImg(saveFileNm, saveUrl);
        memberImg.setMember(saveMember);
        memberImgRepository.save(memberImg);

        //파일저장
        try{
            fileService.uploadFile(multipartFile, checkFolder, uploadUrl);
        }catch (Exception e){
            throw new IllegalStateException("파일을 저장하지 못했습니다.");
        }
    }
    public String renameFile(MultipartFile multipartFile){
        UUID uuid = UUID.randomUUID();
        String oriFileNm = multipartFile.getOriginalFilename(); // 실제파일이름

        String saveFileNm = uuid.toString()+oriFileNm.substring(oriFileNm.lastIndexOf(".")) ;// .후 제거


        return saveFileNm;
    }

    public void updateImg(MultipartFile multipartFile, MemberImg memberImg){
        String oriName = memberImg.getFileName();
        String oriUrl = memberImg.getUrl();

        if(oriName == null || oriUrl == null){
            oriName = renameFile(multipartFile);
            oriUrl = "/image/members"+"/"+oriName; //db url
        }

        //상품 이미지 저장.
        memberImg.updateImg(oriName, oriUrl);
        memberImgRepository.save(memberImg);

        //파일저장
        try{
            fileService.uploadFile(multipartFile, oriName, oriUrl);
        }catch (Exception e){
            throw new IllegalStateException("파일을 저장하지 못했습니다.");
        }

    }


}
