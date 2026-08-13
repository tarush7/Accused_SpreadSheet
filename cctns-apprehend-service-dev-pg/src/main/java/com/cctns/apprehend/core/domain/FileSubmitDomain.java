package com.cctns.apprehend.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class FileSubmitDomain {

    private List<FileSubmitDataDomain> fileUploadDataList;
}
