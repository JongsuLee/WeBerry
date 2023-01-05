package com.weberry.backend.service.report;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.weberry.backend.entity.Data;
import com.weberry.backend.entity.Image;
import com.weberry.backend.entity.Report;
import com.weberry.backend.entity.ReportRequestList;
import com.weberry.backend.entity.User.SignIn;
import com.weberry.backend.repository.DataRepository;
import com.weberry.backend.repository.ImageRepository;
import com.weberry.backend.repository.ReportRepository;

@Service
public class ReportServiceImpl implements ReportService{

	@Autowired
	private ReportRepository reportRepository;

	@Autowired
	private DataRepository dataRepository;
	
	@Autowired
	private ImageRepository imageRepository;
	
	@Override
	public void writeReport(ReportRequestList requestList) {
		
		for (int i = 0; i < requestList.getRequestList().size(); i++) {
			Report.Request request = requestList.getRequestList().get(i);
			Image baseImageUrl = requestList.getBaseImageUrls().get(i);
			Image analyzedImageUrl = requestList.getAnalyzedImageUrls().get(i);
			Report.ToShow report = writeReport(request, baseImageUrl, analyzedImageUrl);
			
			System.out.println(String.format("Report: %s와 같이 저장되었습니다.\n", report));
		}
		
	}
	
	private Report.ToShow writeReport(Report.Request request, Image baseImageUrl, Image analyzedImageUrl) {
		reportRepository.save(Report.Request.toReport(request));
		
		Report savedReport = reportRepository.findById(request.getId()).get();
		imageRepository.save(Image.Request.toImage(baseImageUrl.getImageUrl(), savedReport));
		Image savedBaseImageUrl = imageRepository.findById(baseImageUrl.getImageUrl()).get();
		reportRepository.save(savedBaseImageUrl.setReportBaseUrl(savedReport));
		
		
		savedReport = reportRepository.findById(request.getId()).get();
		imageRepository.save(Image.Request.toImage(analyzedImageUrl.getImageUrl(), savedReport));
		Image savedAnalyzedImageUrl = imageRepository.findById(analyzedImageUrl.getImageUrl()).get();
		reportRepository.save(savedAnalyzedImageUrl.setReportAnalyzedUrl(savedReport));
		
		return Report.ToShow.toShow(reportRepository.findById(request.getId()).get()); 
	}

	@Override
	public List<Report.ToShow> getDailyReports(SignIn user) {
		String farmId = user.getFarm().getFarmId();
		LocalDate today = LocalDate.now();
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyMMdd");
		String id = String.format("%s_%s", farmId, today.format(format));
		
		List<Report.ToShow> toShows = new ArrayList<Report.ToShow>();
		List<Report> reports = reportRepository.findAllByIdStartsWith(id);
		
		try {
			reports.stream().forEach(report -> toShows.add(Report.ToShow.toShow(report)));
			
		} catch (Exception e) {
			System.out.printf("금일 %s에서 측정한 내역이 없습니다.", farmId);
			
		}
		
		return toShows;
	}
	
}
