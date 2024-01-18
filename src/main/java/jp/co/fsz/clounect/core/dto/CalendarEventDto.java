package jp.co.fsz.clounect.core.dto;

import lombok.Data;

import java.util.List;

@Data
public class CalendarEventDto {
  private String calendarId;
  private List<String> eventIds;
}
